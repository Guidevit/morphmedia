package com.example.lhm3d.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Renderer for a 3D model using OpenGL ES.
 */
class Model3DRenderer(context: Context) {

    // Model properties
    private var vertices: FloatBuffer? = null
    private var normals: FloatBuffer? = null
    private var indices: ShortBuffer? = null
    private var texCoords: FloatBuffer? = null
    private var indicesCount = 0
    
    // Transformation matrices
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    
    // Shader program
    private var program = 0
    
    // Shader handles
    private var positionHandle = 0
    private var normalHandle = 0
    private var texCoordHandle = 0
    private var mvpMatrixHandle = 0
    private var lightPosHandle = 0
    private var colorHandle = 0
    
    // Animation properties
    private var isAnimating = false
    private var currentAnimation: String? = null
    private var animationTime = 0f
    private var animationSpeed = 1.0f
    private var loopAnimation = true
    
    // Rotation
    private var rotationAngle = 0f
    
    // Vertex shader
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 aPosition;
        attribute vec3 aNormal;
        attribute vec2 aTexCoord;
        varying vec3 vNormal;
        varying vec2 vTexCoord;
        
        void main() {
            vNormal = aNormal;
            vTexCoord = aTexCoord;
            gl_Position = uMVPMatrix * aPosition;
        }
    """.trimIndent()
    
    // Fragment shader
    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec3 uLightPos;
        uniform vec4 uColor;
        varying vec3 vNormal;
        varying vec2 vTexCoord;
        
        void main() {
            // Simple lighting calculation
            vec3 lightDir = normalize(uLightPos);
            float diff = max(dot(normalize(vNormal), lightDir), 0.1);
            vec3 diffColor = uColor.rgb * diff;
            
            gl_FragColor = vec4(diffColor, uColor.a);
        }
    """.trimIndent()

    init {
        // Initialize matrices
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)
        
        // Create shader program
        createProgram()
    }

    /**
     * Create the shader program.
     */
    private fun createProgram() {
        // Load shaders
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        
        // Create program
        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        
        // Get shader handles
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        normalHandle = GLES20.glGetAttribLocation(program, "aNormal")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        lightPosHandle = GLES20.glGetUniformLocation(program, "uLightPos")
        colorHandle = GLES20.glGetUniformLocation(program, "uColor")
        
        // Release shader resources
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)
    }

    /**
     * Load a shader.
     */
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    /**
     * Set model data for rendering.
     */
    fun setModelData(
        verticesArray: FloatArray,
        normalsArray: FloatArray,
        indicesArray: ShortArray,
        texCoordsArray: FloatArray
    ) {
        // Create buffers
        vertices = ByteBuffer.allocateDirect(verticesArray.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(verticesArray)
                position(0)
            }
        
        normals = ByteBuffer.allocateDirect(normalsArray.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(normalsArray)
                position(0)
            }
        
        indices = ByteBuffer.allocateDirect(indicesArray.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer().apply {
                put(indicesArray)
                position(0)
            }
        
        texCoords = ByteBuffer.allocateDirect(texCoordsArray.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(texCoordsArray)
                position(0)
            }
        
        indicesCount = indicesArray.size
    }

    /**
     * Draw the model.
     */
    fun draw(width: Int, height: Int, deltaTime: Float) {
        // Clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        // Skip if no model data
        if (vertices == null || normals == null || indices == null) {
            return
        }
        
        // Set up the projection matrix
        val aspect = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspect, 0.1f, 100f)
        
        // Update rotation
        rotationAngle += deltaTime * 20f
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, rotationAngle, 0f, 1f, 0f)
        
        // Calculate MVP matrix
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)
        
        // Use the shader program
        GLES20.glUseProgram(program)
        
        // Pass the MVP matrix
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        
        // Pass the light position
        GLES20.glUniform3f(lightPosHandle, 0f, 1f, 1f)
        
        // Pass the color
        GLES20.glUniform4f(colorHandle, 0.7f, 0.7f, 0.7f, 1.0f)
        
        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(normalHandle)
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        
        // Set vertex data
        GLES20.glVertexAttribPointer(
            positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertices
        )
        GLES20.glVertexAttribPointer(
            normalHandle, 3, GLES20.GL_FLOAT, false, 0, normals
        )
        GLES20.glVertexAttribPointer(
            texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoords
        )
        
        // Draw the model
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, indicesCount, GLES20.GL_UNSIGNED_SHORT, indices
        )
        
        // Disable vertex arrays
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    /**
     * Set animation properties.
     */
    fun setAnimation(animationId: String) {
        currentAnimation = animationId
        animationTime = 0f
        isAnimating = true
    }

    /**
     * Set animation speed.
     */
    fun setAnimationSpeed(speed: Float) {
        animationSpeed = speed
    }

    /**
     * Set animation looping.
     */
    fun setAnimationLooping(loop: Boolean) {
        loopAnimation = loop
    }

    /**
     * Start animation playback.
     */
    fun playAnimation() {
        isAnimating = true
    }

    /**
     * Pause animation playback.
     */
    fun pauseAnimation() {
        isAnimating = false
    }

    /**
     * Reset animation to the beginning.
     */
    fun resetAnimation() {
        animationTime = 0f
    }

    /**
     * Update animation.
     */
    fun updateAnimation(deltaTime: Float) {
        if (isAnimating) {
            animationTime += deltaTime * animationSpeed
            
            // In a real app, we would use the animation time
            // to interpolate between keyframes
        }
    }
}
