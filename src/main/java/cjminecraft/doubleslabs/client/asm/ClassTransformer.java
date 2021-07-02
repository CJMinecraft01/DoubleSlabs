package cjminecraft.doubleslabs.client.asm;

import com.google.common.collect.Maps;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class ClassTransformer implements IClassTransformer, Opcodes {

    private static final Map<String, Transformer> TRANSFORMERS = Maps.newHashMap();

    static {
        TRANSFORMERS.put("net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer", ClassTransformer::transformBlockModelRenderer);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (TRANSFORMERS.containsKey(transformedName)) {
            log("Transforming " + transformedName);
            return TRANSFORMERS.get(transformedName).apply(basicClass);
        }

        return basicClass;
    }

    private static byte[] transformBlockModelRenderer(byte[] basicClass) {
        MethodSignature sig1 = new MethodSignature("renderModelSmooth", "func_187498_b", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z");
        MethodSignature sig2 = new MethodSignature("renderModelFlat", "func_187497_c", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z");

        return transform(basicClass,
                forMethod(sig1, (MethodNode method) -> {
                            InsnList newInstructions = new InsnList();

                            LabelNode escape = new LabelNode();

                            newInstructions.add(new VarInsnNode(ALOAD, 0));
                            newInstructions.add(new VarInsnNode(ALOAD, 1));
                            newInstructions.add(new VarInsnNode(ALOAD, 2));
                            newInstructions.add(new VarInsnNode(ALOAD, 3));
                            newInstructions.add(new VarInsnNode(ALOAD, 4));
                            newInstructions.add(new VarInsnNode(ALOAD, 5));
                            newInstructions.add(new VarInsnNode(ILOAD, 6));
                            newInstructions.add(new VarInsnNode(LLOAD, 7));
                            newInstructions.add(new MethodInsnNode(INVOKESTATIC,
                                    "cjminecraft/doubleslabs/client/asm/BlockModelRendererMixin",
                                    "renderModelSmooth",
                                    "(Lnet/minecraft/client/renderer/BlockModelRenderer;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z",
                                    false));
                            newInstructions.add(new InsnNode(DUP));
                            newInstructions.add(new JumpInsnNode(IFEQ, escape));
                            newInstructions.add(new InsnNode(IRETURN));
                            newInstructions.add(escape);
                            newInstructions.add(new InsnNode(POP));

                            method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
                            return true;
                        }
                ),
                forMethod(sig2, (MethodNode method) -> {
                    InsnList newInstructions = new InsnList();

                    LabelNode escape = new LabelNode();

                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
                    newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
                    newInstructions.add(new VarInsnNode(Opcodes.LLOAD, 7));
                    newInstructions.add(new MethodInsnNode(INVOKESTATIC,
                            "cjminecraft/doubleslabs/client/asm/BlockModelRendererMixin",
                            "renderModelFlat",
                            "(Lnet/minecraft/client/renderer/BlockModelRenderer;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z",
                            false
                    ));
                    newInstructions.add(new InsnNode(Opcodes.DUP));
                    newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, escape));
                    newInstructions.add(new InsnNode(Opcodes.IRETURN));
                    newInstructions.add(escape);
                    newInstructions.add(new InsnNode(Opcodes.POP));

                    method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);

                    return true;
                })
            );
    }

    private static void log(String str) {
        LogManager.getLogger("DoubleSlabs ASM").info(str);
    }

    private static byte[] transform(byte[] basicClass, TransformerAction... methods) {
        ClassReader reader;
        try {
            reader = new ClassReader(basicClass);
        } catch (NullPointerException ex) {
            return basicClass;
        }

        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        boolean didAnything = false;

        for (TransformerAction pair : methods)
            didAnything |= pair.test(node);

        if (didAnything) {
            ClassWriter writer = new SafeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

    // From Quark 1.12

    public static boolean findMethodAndTransform(ClassNode node, MethodSignature sig, MethodAction predicate, boolean logResult) {
        for (MethodNode method : node.methods) {
            if (sig.matches(method)) {
                log("Located Method, patching...");

                boolean finish = predicate.test(method);
                if (logResult)
                    log("Patch result: " + finish);

                return finish;
            }
        }

        if (logResult)
            log("Failed to locate the method!");
        return false;
    }

    private static TransformerAction forMethod(MethodSignature sig, MethodAction... actions) {
        return new MethodTransformerAction(sig, actions);
    }

    private static String getNodeString(AbstractInsnNode node) {
        Printer printer = new Textifier();

        TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
        node.accept(visitor);

        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();

        return sw.toString().replaceAll("\n", "").trim();
    }

    public static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
        Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

        boolean didAny = false;
        while (iterator.hasNext()) {
            AbstractInsnNode anode = iterator.next();
            if (filter.test(anode)) {
                log("Located patch target node " + getNodeString(anode));
                didAny = true;
                if (action.test(method, anode))
                    break;
            }
        }

        return didAny;
    }

    // Basic interface aliases to not have to clutter up the code with generics over and over again
    private interface Transformer extends Function<byte[], byte[]> {
        // NO-OP
    }

    private interface TransformerAction extends Predicate<ClassNode> {
        // NO-OP
    }

    private interface MethodAction extends Predicate<MethodNode> {
        // NO-OP
    }

    private interface NodeFilter extends Predicate<AbstractInsnNode> {
        // NO-OP
    }

    private interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> {
        // NO-OP
    }

    public static class MethodSignature {
        private final String funcName, srgName, funcDesc;

        public MethodSignature(String funcName, String srgName, String funcDesc) {
            this.funcName = funcName;
            this.srgName = srgName;
            this.funcDesc = funcDesc;
        }

        @Override
        public String toString() {
            return "Names [" + funcName + ", " + srgName + "] Descriptor " + funcDesc;
        }

        public boolean matches(String methodName, String methodDesc) {
            return (methodName.equals(funcName) || methodName.equals(srgName))
                    && (methodDesc.equals(funcDesc));
        }

        public boolean matches(MethodNode method) {
            return matches(method.name, method.desc);
        }

        public boolean matches(MethodInsnNode method) {
            return matches(method.name, method.desc);
        }

        public String mappedName(String owner) {
            return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, srgName, funcDesc);
        }

    }

    /**
     * Safe class writer.
     * The way COMPUTE_FRAMES works may require loading additional classes. This can cause ClassCircularityErrors.
     * The override for getCommonSuperClass will ensure that COMPUTE_FRAMES works properly by using the right ClassLoader.
     * <p>
     * Code from: https://github.com/JamiesWhiteShirt/clothesline/blob/master/src/core/java/com/jamieswhiteshirt/clothesline/core/SafeClassWriter.java
     */
    public static class SafeClassWriter extends ClassWriter {
        public SafeClassWriter(int flags) {
            super(flags);
        }

        @Override
        protected String getCommonSuperClass(String type1, String type2) {
            Class<?> c, d;
            ClassLoader classLoader = Launch.classLoader;
            try {
                c = Class.forName(type1.replace('/', '.'), false, classLoader);
                d = Class.forName(type2.replace('/', '.'), false, classLoader);
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
            if (c.isAssignableFrom(d)) {
                return type1;
            }
            if (d.isAssignableFrom(c)) {
                return type2;
            }
            if (c.isInterface() || d.isInterface()) {
                return "java/lang/Object";
            } else {
                do {
                    c = c.getSuperclass();
                } while (!c.isAssignableFrom(d));
                return c.getName().replace('.', '/');
            }
        }
    }

    private static class MethodTransformerAction implements TransformerAction {
        private final MethodSignature sig;
        private final MethodAction[] actions;

        public MethodTransformerAction(MethodSignature sig, MethodAction[] actions) {
            this.sig = sig;
            this.actions = actions;
        }

        @Override
        public boolean test(ClassNode classNode) {
            boolean didAnything = false;
            log("Applying Transformation to method (" + sig + ")");
            for (MethodAction action : actions)
                didAnything |= findMethodAndTransform(classNode, sig, action, true);
            return didAnything;
        }
    }

}
