package cs4620.gl;

import java.util.ArrayList;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cs4620.ray1.IntersectionRecord;
import cs4620.ray1.Ray;
import cs4620.ray1.surface.Mesh;
import cs4620.ray1.surface.Surface;
import cs4620.common.Scene;
import cs4620.common.UniqueContainer;
import cs4620.common.event.SceneTransformationEvent;
import cs4620.mesh.MeshData;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;
import egl.math.Vector3d;
import egl.math.Vector4;

public class CameraController {
	protected final Scene scene;
	public RenderCamera camera;
	protected final RenderEnvironment rEnv;

	protected boolean prevFrameButtonDown = false;
	protected int prevMouseX, prevMouseY;

	protected boolean orbitMode = false;

	private float rotationX = 0;
	private List<Mesh> meshes;
	private float nearZPos;

	public CameraController(Scene s, RenderEnvironment re, RenderCamera c) {
		scene = s;
		rEnv = re;
		camera = c;
		meshes = new ArrayList<Mesh>();
		for(cs4620.common.Mesh m : scene.meshes) {
			MeshData md = new MeshData();
			if(m.type == cs4620.common.Mesh.Type.GENERATOR){
				System.out.println("Adding Generated Data");
				Mesh me = new Mesh(m.data);
				meshes.add(me);
			}
			else{
				System.out.println("Adding File Data");
				Mesh me = new Mesh(md);
				if(!(m.file == null)) {
					me.setData(m.file);
					System.out.println("Adding a mesh! its real!");
				}

			}
		}

		nearZPos = (float) - 0.02;
	}

	private void pickUpObject(Matrix4 pMat, Matrix4 camTransf) {
		Vector2 curMousePos = new Vector2(prevMouseX, prevMouseY).add(0.5f).mul(2).div(camera.viewportSize.x, camera.viewportSize.y).sub(1);
		System.out.println(curMousePos);
		//System.out.println("pickUpObject called");
		Vector4 v4 = new Vector4(curMousePos.x, curMousePos.y, nearZPos, 1);
	//	Vector4 v4 = pMat.clone().mulBefore(camTransf).clone().mul(mousePos);
	//	Vector4 origin = new Vector4(0, 0, 0, 1);
	//	Vector4 o4 = pMat.clone().mulBefore(camTransf).clone().mul(origin);
		Vector3d v = new Vector3d(v4.x, v4.y, v4.z);
		Vector3d o = new Vector3d(0,0,0);
		Ray rayIn = new Ray(o, v);
		//System.out.println(v);
		IntersectionRecord outRecord = new IntersectionRecord();
		boolean anyIntersection = true;
		int firstTime = 0;
		double end = 0;
		for(Mesh m : meshes) {
			ArrayList<Surface> list = new ArrayList<Surface>();
			m.appendRenderableSurfaces(list);
			for(Surface surf : list) {
				IntersectionRecord inRecord = new IntersectionRecord();
				Ray copyRay = new Ray(rayIn);
				if(surf.intersect(inRecord, copyRay)) {
					if(anyIntersection) {
						outRecord.set(inRecord);
								}
					//check outRecord intersection point and compare with end of ray
					if(firstTime == 0) {
						outRecord.set(inRecord);
						end = inRecord.t;
						firstTime++;
					} 			
					else if(inRecord.t <= end) {
						end = inRecord.t;
						outRecord.set(inRecord);
					}
				}
			
			}
	}
}

/**
 * Update the camera's transformation matrix in response to user input.
 * 
 * Pairs of keys are available to translate the camera in three direction oriented to the camera,
 * and to rotate around three axes oriented to the camera.  Mouse input can also be used to rotate 
 * the camera around the horizontal and vertical axes.  All effects of these controls are achieved
 * by altering the transformation stored in the SceneCamera that is referenced by the RenderCamera
 * this controller is associated with.
 * 
 * @param et  time elapsed since previous frame
 */
public void update(double et) {

	Vector3 motion = new Vector3();
	Vector3 rotation = new Vector3();

	if(Keyboard.isKeyDown(Keyboard.KEY_W)) { motion.add(0, 0, -1); }
	if(Keyboard.isKeyDown(Keyboard.KEY_S)) { motion.add(0, 0, 1); }
	if(Keyboard.isKeyDown(Keyboard.KEY_A)) { motion.add(-1, 0, 0); }
	if(Keyboard.isKeyDown(Keyboard.KEY_D)) { motion.add(1, 0, 0); }
	//if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) { motion.add(0, -1, 0); }
	//if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { motion.add(0, 1, 0); }

	//if(Keyboard.isKeyDown(Keyboard.KEY_E)) { rotation.add(0, 0, -1); }
	//if(Keyboard.isKeyDown(Keyboard.KEY_Q)) { rotation.add(0, 0, 1); }
	if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) { rotation.add(-1, 0, 0); }
	if(Keyboard.isKeyDown(Keyboard.KEY_UP)) { rotation.add(1, 0, 0); }
	if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) { rotation.add(0, -1, 0); }
	if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) { rotation.add(0, 1, 0); }

	//if(Keyboard.isKeyDown(Keyboard.KEY_O)) { orbitMode = true; } 
	if(Keyboard.isKeyDown(Keyboard.KEY_F)) { orbitMode = false; } 

	boolean thisFrameButtonDown = Mouse.isButtonDown(0) && !(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL));
	int thisMouseX = Mouse.getX(), thisMouseY = Mouse.getY();
	/*if (thisFrameButtonDown && prevFrameButtonDown) {
			rotation.add(0, -0.1f * (thisMouseX - prevMouseX), 0);
			rotation.add(0.1f * (thisMouseY - prevMouseY), 0, 0);
		}*/
	prevFrameButtonDown = thisFrameButtonDown;
	
	prevMouseX = thisMouseX;
	prevMouseY = thisMouseY;

	//System.out.println(prevMouseX);
	//System.out.println(prevMouseY);




	RenderObject parent = rEnv.findObject(scene.objects.get(camera.sceneObject.parent));
	Matrix4 pMat = parent == null ? new Matrix4() : parent.mWorldTransform;
	pickUpObject(pMat, camera.sceneObject.transformation);
	if(motion.lenSq() > 0.01) {
		motion.normalize();
		motion.mul(5 * (float)et);
		translate(pMat, camera.sceneObject.transformation, motion);
	}
	if(rotation.lenSq() > 0.01) {
		rotation.mul((float)(100.0 * et));
		rotate(pMat, camera.sceneObject.transformation, rotation);
	}
	scene.sendEvent(new SceneTransformationEvent(camera.sceneObject));
}

/**
 * Apply a rotation to the camera.
 * 
 * Rotate the camera about one ore more of its local axes, by modifying <b>transformation</b>.  The 
 * camera is rotated by rotation.x about its horizontal axis, by rotation.y about its vertical axis, 
 * and by rotation.z around its view direction.  The rotation is about the camera's viewpoint, if 
 * this.orbitMode is false, or about the world origin, if this.orbitMode is true.
 * 
 * @param parentWorld  The frame-to-world matrix of the camera's parent
 * @param transformation  The camera's transformation matrix (in/out parameter)
 * @param rotation  The rotation in degrees, as Euler angles (rotation angles about x, y, z axes)
 */
protected void rotate(Matrix4 parentWorld, Matrix4 transformation, Vector3 rotation) {
	// TODO#A3 SOLUTION START

	if((rotationX > 45 && rotation.x > 0) || (rotationX < -45 && rotation.x < 0)) {
		rotation.x = 0;
	}
	rotationX += rotation.x;

	rotation = rotation.clone().mul((float)(Math.PI / 180.0));
	Matrix4 mRot = Matrix4.createRotationX(rotation.x);
	mRot.mulAfter(Matrix4.createRotationY(rotation.y));
	mRot.mulAfter(Matrix4.createRotationZ(rotation.z));

	if (orbitMode) {
		Vector3 rotCenter = new Vector3(0,0,0);
		transformation.clone().invert().mulPos(rotCenter);
		parentWorld.clone().invert().mulPos(rotCenter);
		mRot.mulBefore(Matrix4.createTranslation(rotCenter.clone().negate()));
		mRot.mulAfter(Matrix4.createTranslation(rotCenter));
	}
	transformation.mulBefore(mRot);

	System.out.println(rotation.x);
	// SOLUTION END
}

/**
 * Apply a translation to the camera.
 * 
 * Translate the camera by an offset measured in camera space, by modifying <b>transformation</b>.
 * @param parentWorld  The frame-to-world matrix of the camera's parent
 * @param transformation  The camera's transformation matrix (in/out parameter)
 * @param motion  The translation in camera-space units
 */
protected void translate(Matrix4 parentWorld, Matrix4 transformation, Vector3 motion) {
	// TODO#A3 SOLUTION START

	Matrix4 mTrans = Matrix4.createTranslation(motion);

	transformation.mulBefore(mTrans);

	// SOLUTION END
}




}
