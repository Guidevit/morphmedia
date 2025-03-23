package com.example.lhm3d.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * OpenGL renderer that uses Model3DRenderer to render 3D models.
 */
class GLSurfaceRenderer(private val context: Context) : GLSurfaceView.Renderer {
    
    private val TAG = "GLSurfaceRenderer"
    
    // 3D model renderer
    private val model3DRenderer = Model3DRenderer(context)
    
    // Model path
    private var modelPath: String? = null
    
    // Animation ID
    private var animationId: String? = null
    
    // Timing
    private var lastFrameTime = System.nanoTime()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the background color
        GLES20.glClearColor(0.95f, 0.95f, 0.95f, 1.0f)
        
        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        
        // Enable face culling
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        
        // Create a simple cube for testing
        // In a real app, this would be replaced with actual model data
        createTestCube()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Set the viewport
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        // Calculate delta time
        val currentTime = System.nanoTime()
        val deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f // Convert to seconds
        lastFrameTime = currentTime
        
        // Update animation
        model3DRenderer.updateAnimation(deltaTime)
        
        // Get viewport dimensions
        val viewport = IntArray(4)
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0)
        
        // Draw the model
        model3DRenderer.draw(viewport[2], viewport[3], deltaTime)
    }

    /**
     * Set the model path.
     */
    fun setModelPath(path: String) {
        modelPath = path
        
        // In a real app, we would load the model from the path
        // For now, we'll use the test cube
    }

    /**
     * Set the animation.
     */
    fun setAnimation(id: String) {
        animationId = id
        model3DRenderer.setAnimation(id)
    }

    /**
     * Start animation playback.
     */
    fun playAnimation() {
        model3DRenderer.playAnimation()
    }

    /**
     * Pause animation playback.
     */
    fun pauseAnimation() {
        model3DRenderer.pauseAnimation()
    }

    /**
     * Reset animation.
     */
    fun resetAnimation() {
        model3DRenderer.resetAnimation()
    }

    /**
     * Set animation speed.
     */
    fun setAnimationSpeed(speed: Float) {
        model3DRenderer.setAnimationSpeed(speed)
    }

    /**
     * Set animation looping.
     */
    fun setAnimationLooping(loop: Boolean) {
        model3DRenderer.setAnimationLooping(loop)
    }

    /**
     * Create a test cube for rendering.
     */
    private fun createTestCube() {
        try {
            // Vertices for a cube
            val vertices = floatArrayOf(
                // Front face
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                
                // Back face
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                
                // Top face
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                
                // Bottom face
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                
                // Right face
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                
                // Left face
                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, -1.0f
            )
            
            // Scale down the cube
            for (i in vertices.indices) {
                vertices[i] *= 0.5f
            }
            
            // Normals for the cube
            val normals = floatArrayOf(
                // Front face
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                
                // Back face
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                
                // Top face
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                
                // Bottom face
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                
                // Right face
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                
                // Left face
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f
            )
            
            // Texture coordinates for the cube
            val texCoords = floatArrayOf(
                // Front face
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                
                // Back face
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,
                
                // Top face
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                
                // Bottom face
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                
                // Right face
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,
                
                // Left face
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
            )
            
            // Indices for the cube
            val indices = shortArrayOf(
                0, 1, 2, 0, 2, 3,       // Front face
                4, 5, 6, 4, 6, 7,       // Back face
                8, 9, 10, 8, 10, 11,    // Top face
                12, 13, 14, 12, 14, 15, // Bottom face
                16, 17, 18, 16, 18, 19, // Right face
                20, 21, 22, 20, 22, 23  // Left face
            )
            
            // Set the model data
            model3DRenderer.setModelData(vertices, normals, indices, texCoords)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating test cube", e)
        }
    }
}
