function initializeCoreMod() {
    return {
        'override-dowse-fire': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.entity.projectile.ThrownPotion',
                'methodName': 'm_150192_', // dowseFire
                'methodDesc': '(Lnet/minecraft/core/BlockPos;)V'
            },
            'transformer': function (method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

                var startInstructions = new InsnList();

                startInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                startInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                startInstructions.add(ASM.buildMethodCall(
                    "cjminecraft/doubleslabs/mixin/ThrownPotionMixin",
                    "dowseFire",
                    "(Lnet/minecraft/world/entity/projectile/ThrownPotion;Lnet/minecraft/core/BlockPos;)V",
                    ASM.MethodType.STATIC
                ));
                method.instructions.insertBefore(method.instructions.getFirst(), startInstructions);

                return method;
            }
        }
    }
}