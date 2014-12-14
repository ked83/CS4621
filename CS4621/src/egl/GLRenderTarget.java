package egl;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;
import org.lwjgl.opengl.*;


import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;

import egl.GL.DrawBufferMode;
import egl.GL.FramebufferAttachment;
import egl.GL.FramebufferErrorCode;
import egl.GL.FramebufferTarget;
import egl.GL.TextureTarget;
import egl.GL.TextureUnit;

public class GLRenderTarget extends GLTexture {
	private int fb, texDepth;

    public GLRenderTarget(boolean init) {
    	super(TextureTarget.Texture2D, init);
    }
    public GLRenderTarget() {
    	this(false);
    }

    public void buildRenderTarget() {
        fb = glGenFramebuffers();
        glBindFramebuffer(FramebufferTarget.Framebuffer, fb);

        bind();
        glFramebufferTexture2D(FramebufferTarget.Framebuffer, FramebufferAttachment.ColorAttachment0, getTarget(), getID(), 0);
        unbind();

        texDepth = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texDepth);
        ByteBuffer bb = null;
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, getWidth(), getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, bb);
        SamplerState.POINT_CLAMP.set(GL_TEXTURE_2D);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, texDepth, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        glDrawBuffer(DrawBufferMode.ColorAttachment0);

        int err = glCheckFramebufferStatus(FramebufferTarget.Framebuffer);
        if(err != FramebufferErrorCode.FramebufferComplete)
            return;

        glBindFramebuffer(FramebufferTarget.Framebuffer, 0);
    }
    
    public void useTarget() {
    	glBindFramebuffer(FramebufferTarget.Framebuffer, fb);
    	glViewport(0, 0, getWidth(), getHeight());
    }
    public static void unuseTarget() {
    	glBindFramebuffer(FramebufferTarget.Framebuffer, 0);
    }
    
    //start Pablo
    public void useDepth(int texUnit, int unTex) {
    	glActiveTexture(texUnit);
    	glBindTexture(GL11.GL_TEXTURE_2D, texDepth);
    	glUniform1i(unTex, texUnit-TextureUnit.Texture0);
    }
    //end Pablo
}