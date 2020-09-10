package cjminecraft.doubleslabs.client.util;

import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.math.MathHelper;

public class ExtendedVector4f extends Vector4f {

    public ExtendedVector4f() {
        super();
    }

    public ExtendedVector4f(float x, float y, float z, float w) {
        super(x, y, z, w);
    }

    public boolean normalize() {
        float f = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
        if ((double)f < 1.0E-5D) {
            return false;
        } else {
            float f1 = (float) MathHelper.fastInvSqrt(f);
            this.set(this.getX() * f1, this.getY() * f1, this.getZ() * f1, this.getW() * f1);
            return true;
        }
    }

}
