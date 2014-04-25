package erebus.client.model.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelCicada extends ModelBase {
	ModelRenderer Test;

	public ModelCicada() {
	    textureWidth = 64;
	    textureHeight = 64;
	    
	      Test = new ModelRenderer(this, 0, 0);
	      Test.addBox(-5F, 0F, -11F, 10, 10, 22);
	      Test.setRotationPoint(0F, 14F, 0F);
	      Test.setTextureSize(64, 64);
	      Test.mirror = true;
	      setRotation(Test, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, prevLimbSwing, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, prevLimbSwing, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		GL11.glPushMatrix();
		GL11.glTranslated(0F, -0.5F, 0 );
		Test.render(unitPixel);
		GL11.glPopMatrix();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, prevLimbSwing, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
	}
}
