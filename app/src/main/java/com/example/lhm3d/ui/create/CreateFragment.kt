package com.example.lhm3d.ui.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lhm3d.R
import com.example.lhm3d.databinding.FragmentCreateBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: CreateViewModel
    
    private var currentPhotoPath: String? = null
    private var selectedImageFile: File? = null
    
    // UI elements
    private var uploadButton: com.google.android.material.button.MaterialButton? = null
    private var createButton: com.google.android.material.button.MaterialButton? = null
    private var progressBar: android.widget.ProgressBar? = null
    private var imagePreview: android.widget.ImageView? = null
    private var textImagePlaceholder: android.widget.LinearLayout? = null
    private var editTextName: com.google.android.material.textfield.TextInputEditText? = null
    private var editTextDescription: com.google.android.material.textfield.TextInputEditText? = null
    private var switchPublic: android.widget.Switch? = null
    
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(requireContext(), "Storage permission is required to select images", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let { path ->
                selectedImageFile = File(path)
                updateImagePreview(Uri.fromFile(selectedImageFile))
            }
        }
    }
    
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Convert Uri to File
                val file = createTempFileFromUri(uri)
                selectedImageFile = file
                updateImagePreview(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create a ViewModel factory to pass the context
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreateViewModel(requireContext()) as T
            }
        }
        
        viewModel = ViewModelProvider(this, factory).get(CreateViewModel::class.java)

        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        observeViewModel()

        return root
    }

    private fun setupUI() {
        // Find control elements
        val uploadButton = binding.root.findViewById<com.google.android.material.button.MaterialButton>(R.id.upload_button)
        val createButton = binding.root.findViewById<com.google.android.material.button.MaterialButton>(R.id.create_button)
        val progressBar = binding.root.findViewById<android.widget.ProgressBar>(R.id.loading_indicator)
        val imagePreview = binding.root.findViewById<android.widget.ImageView>(R.id.preview_image)
        val textImagePlaceholder = binding.root.findViewById<android.widget.LinearLayout>(R.id.placeholder_container)
        val editTextName = binding.root.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.model_name)
        val editTextDescription = binding.root.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.model_description)
        // Switch might not exist in the layout yet, creating a default value
        val switchPublic = null // Temporarily removed switch reference
        
        // Store references
        this.uploadButton = uploadButton
        this.createButton = createButton
        this.progressBar = progressBar
        this.imagePreview = imagePreview
        this.textImagePlaceholder = textImagePlaceholder
        this.editTextName = editTextName
        this.editTextDescription = editTextDescription
        this.switchPublic = switchPublic
        
        // Handle click events
        if (uploadButton != null) {
            uploadButton.setOnClickListener { checkStoragePermission() }
        }
        
        if (createButton != null) {
            createButton.setOnClickListener { submitForm() }
        }
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
            createButton?.isEnabled = !isLoading
        }
        
        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { model ->
                    Toast.makeText(requireContext(), "Model created successfully!", Toast.LENGTH_SHORT).show()
                    // Navigate to model viewer or clear form
                    clearForm()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(requireContext(), "Camera permission is needed to take photos", Toast.LENGTH_SHORT).show()
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun checkStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(requireContext(), "Storage permission is needed to select images", Toast.LENGTH_SHORT).show()
                requestStoragePermissionLauncher.launch(permission)
            }
            else -> {
                requestStoragePermissionLauncher.launch(permission)
            }
        }
    }
    
    private fun launchCamera() {
        val photoFile = createImageFile()
        photoFile?.let {
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                it
            )
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takePictureLauncher.launch(takePictureIntent)
        }
    }
    
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }
    
    private fun createImageFile(): File? {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
            currentPhotoPath = image.absolutePath
            return image
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error creating image file: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }
    
    private fun createTempFileFromUri(uri: Uri): File? {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(requireContext().cacheDir, "JPEG_${timeStamp}.jpg")
        
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        return file
    }
    
    private fun updateImagePreview(uri: Uri) {
        imagePreview?.setImageURI(uri)
        textImagePlaceholder?.visibility = View.GONE
    }
    
    private fun submitForm() {
        // Validate the form
        val name = editTextName?.text.toString().trim()
        val description = editTextDescription?.text.toString().trim()
        val isPublic = switchPublic?.isChecked ?: false
        
        if (name.isEmpty()) {
            editTextName?.error = "Name is required"
            return
        }
        
        if (selectedImageFile == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Submit to view model
        selectedImageFile?.let { file ->
            // Convert File to Uri
            val imageUri = Uri.fromFile(file)
            viewModel.createModel(name, description, imageUri, isPublic)
        }
    }
    
    private fun clearForm() {
        editTextName?.text?.clear()
        editTextDescription?.text?.clear()
        imagePreview?.setImageResource(android.R.drawable.ic_menu_report_image)
        textImagePlaceholder?.visibility = View.VISIBLE
        selectedImageFile = null
        currentPhotoPath = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}