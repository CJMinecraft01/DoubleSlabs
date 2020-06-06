package cjminecraft.doubleslabs.util;

import cjminecraft.doubleslabs.Utils;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.ReadableVector3f;

public class Vector3f extends org.lwjgl.util.vector.Vector3f {

    public static Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
    public static Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
    public static Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
    public static Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
    public static Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);
    public static Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);

    public Vector3f() {
        super();
    }

    public Vector3f(ReadableVector3f src) {
        super(src);
    }

    public Vector3f(float x, float y, float z) {
        super(x, y, z);
    }

    public void transform(Quaternion quaternionIn) {
        Quaternion quaternion = Quaternion.mul(quaternionIn, new Quaternion(this.x, this.y, this.z, 0.0F), null);
        Quaternion quaternion1 = Quaternion.negate(quaternionIn, null);
        Quaternion result = Quaternion.mul(quaternion, quaternion1, null);
        this.set(quaternion.getX(), quaternion.getY(), quaternion.getZ());
    }

    public Quaternion rotation(float angle) {
        return Utils.fromAngle(this, angle, false);
    }

    public Quaternion rotationDegrees(float angle) {
        return Utils.fromAngle(this, angle, true);
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

}
