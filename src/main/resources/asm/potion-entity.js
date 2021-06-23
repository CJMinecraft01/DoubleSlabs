function initializeCoreMod() {
    return {
        'override-extinguish-fires': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.projectile.PotionEntity',
                'methodName': 'func_184542_a', // extinguishFires
                'methodDesc': '(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)V'
            },
            'transformer': function (method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

                var startInstructions = new InsnList();

                startInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                startInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                startInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                startInstructions.add(ASM.buildMethodCall(
                    "cjminecraft/doubleslabs/mixin/PotionEntityMixin",
                    "extinguishFires",
                    "(Lnet/minecraft/entity/projectile/PotionEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)V",
                    ASM.MethodType.STATIC
                ));
                method.instructions.insertBefore(method.instructions.getFirst(), startInstructions);

                return method;
            }
        }
    }
}