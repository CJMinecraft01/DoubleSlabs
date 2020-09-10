package cjminecraft.doubleslabs.client.util.vertex;

import cjminecraft.doubleslabs.client.ClientConstants;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.VertexTransformer;

public class TintOffsetTransformer extends VertexTransformer {

    private final boolean positive;

    public TintOffsetTransformer(IVertexConsumer parent, boolean positive) {
        super(parent);
        this.positive = positive;
    }

    @Override
    public void setQuadTint(int tint) {
        super.setQuadTint(this.positive ? tint + ClientConstants.TINT_OFFSET : tint);
    }
}
