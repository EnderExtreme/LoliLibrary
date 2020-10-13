package zone.rong.lolilib.enderio.mixin;

import com.enderio.core.common.vecmath.Vector4i;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PowerBarOverlayRenderHelper.class)
public class PowerBarOverlayRenderHelperMixin {

    @Shadow(remap = false) protected Vector4i colorBarLeft = new Vector4i(0x2c, 0x1d, 0x13, 255); // Dark Brown - 2C1D13
    @Shadow(remap = false) protected Vector4i colorBarRight = new Vector4i(0x78, 0x69, 0x65, 255); // Light Brown - 786965

}