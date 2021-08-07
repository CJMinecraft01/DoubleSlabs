function initializeCoreMod() {
    return {
        'override-tesselate-with-ao': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.block.ModelBlockRenderer',
                'methodName': 'tesselateWithAO',
                'methodDesc': '(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z'
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
                    "cjminecraft/doubleslabs/mixin/ModelBlockRendererMixin",
                    "tesselateWithAO",
                    "(Lnet/minecraft/client/renderer/block/ModelBlockRenderer;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z",
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
                'class': 'net.minecraft.client.renderer.block.ModelBlockRenderer',
                'methodName': 'tesselateWithoutAO',
                'methodDesc': '(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z'
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
                    "cjminecraft/doubleslabs/mixin/ModelBlockRendererMixin",
                    "tesselateWithoutAO",
                    "(Lnet/minecraft/client/renderer/block/ModelBlockRenderer;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z",
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