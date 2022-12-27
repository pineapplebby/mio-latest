/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL20
 */
package me.mioclient.api.util.render.shader.framebuffer.impl;

import me.mioclient.api.util.Wrapper;
import me.mioclient.api.util.render.shader.framebuffer.FramebufferShader;
import org.lwjgl.opengl.GL20;

public class ItemShader
extends FramebufferShader {
    public static ItemShader INSTANCE = new ItemShader();
    public float mix;
    public float alpha = 1.0f;
    public boolean model;

    public ItemShader() {
        super("glow.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("color");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
        this.setupUniform("dimensions");
        this.setupUniform("mixFactor");
        this.setupUniform("minAlpha");
        this.setupUniform("inside");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i((int)this.getUniform("texture"), (int)0);
        GL20.glUniform1i((int)this.getUniform("inside"), (int)(this.model ? 1 : 0));
        GL20.glUniform2f((int)this.getUniform("texelSize"), (float)(1.0f / (float)Wrapper.mc.displayWidth * (this.radius * this.quality)), (float)(1.0f / (float)Wrapper.mc.displayHeight * (this.radius * this.quality)));
        GL20.glUniform3f((int)this.getUniform("color"), (float)this.red, (float)this.green, (float)this.blue);
        GL20.glUniform1f((int)this.getUniform("divider"), (float)140.0f);
        GL20.glUniform1f((int)this.getUniform("radius"), (float)this.radius);
        GL20.glUniform1f((int)this.getUniform("maxSample"), (float)10.0f);
        GL20.glUniform2f((int)this.getUniform("dimensions"), (float)Wrapper.mc.displayWidth, (float)Wrapper.mc.displayHeight);
        GL20.glUniform1f((int)this.getUniform("mixFactor"), (float)this.mix);
        GL20.glUniform1f((int)this.getUniform("minAlpha"), (float)this.alpha);
    }
}

