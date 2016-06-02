package erebus.client.render.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erebus.client.model.entity.ModelLeech;
import erebus.entity.EntityLeech;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderLeech extends RenderLiving {

	private static final ResourceLocation texture = new ResourceLocation("erebus:textures/entity/leechHungry.png");

	public RenderLeech() {
		super(new ModelLeech(), 0.5F);
	}

	protected void preRenderCallback(EntityLivingBase entity, float par2) {
		EntityLeech leech = (EntityLeech) entity;
		if (!leech.isRiding())
			GL11.glScalef(1 + leech.getBloodConsumed() * 0.1F, 1 + leech.getBloodConsumed() * 0.1F, leech.moveProgress * leech.moveProgress / 2 + 1F);

		if (leech.isRiding()) {
			GL11.glScalef(leech.moveProgress * leech.moveProgress / 2 + 0.5F, leech.moveProgress * leech.moveProgress / 2 + 0.5F, 1F);
			GL11.glRotatef(180, 0, 1, 0);
			GL11.glTranslatef(0, 0, 0.5F);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}
