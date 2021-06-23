function initializeCoreMod() {
    return {
        'override-render-model-smooth': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.BlockModelRenderer',
                'methodName': 'renderModelSmooth',
                'methodDesc': '(Lnet/minecraft/world/ILightReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z'
            },
            'transformer': function (method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

                var newInstructions = new InsnList();

                var escape = new LabelNode();

                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
                newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 7));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 8));
                newInstructions.add(new VarInsnNode(Opcodes.LLOAD, 9));
                newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 11));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 12));
                newInstructions.add(ASM.buildMethodCall(
                    "cjminecraft/doubleslabs/mixin/BlockModelRendererMixin",
                    "renderModelSmooth",
                    "(Lnet/minecraft/client/renderer/BlockModelRenderer;Lnet/minecraft/world/ILightReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z",
                    ASM.MethodType.STATIC));
                newInstructions.add(new InsnNode(Opcodes.DUP));
                newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, escape));
                newInstructions.add(new InsnNode(Opcodes.IRETURN));
                newInstructions.add(escape);
                newInstructions.add(new InsnNode(Opcodes.POP));

                method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
                return method;
            }
        },
        'override-render-model-flat': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.BlockModelRenderer',
                'methodName': 'renderModelFlat',
                'methodDesc': '(Lnet/minecraft/world/ILightReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z'
            },
            'transformer': function (method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

                var newInstructions = new InsnList();

                var escape = new LabelNode();

                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
                newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 7));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 8));
                newInstructions.add(new VarInsnNode(Opcodes.LLOAD, 9));
                newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 11));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 12));
                newInstructions.add(ASM.buildMethodCall(
                    "cjminecraft/doubleslabs/mixin/BlockModelRendererMixin",
                    "renderModelFlat",
                    "(Lnet/minecraft/client/renderer/BlockModelRenderer;Lnet/minecraft/world/ILightReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z",
                    ASM.MethodType.STATIC));
                newInstructions.add(new InsnNode(Opcodes.DUP));
                newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, escape));
                newInstructions.add(new InsnNode(Opcodes.IRETURN));
                newInstructions.add(escape);
                newInstructions.add(new InsnNode(Opcodes.POP));

                method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
                return method;
            }
        }
    }
}