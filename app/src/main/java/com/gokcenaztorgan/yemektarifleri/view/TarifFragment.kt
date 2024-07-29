package com.gokcenaztorgan.yemektarifleri.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.gokcenaztorgan.yemektarifleri.databinding.FragmentTarifBinding
import com.gokcenaztorgan.yemektarifleri.model.Tarif
import com.gokcenaztorgan.yemektarifleri.roomdb.TarifDAO
import com.gokcenaztorgan.yemektarifleri.roomdb.TarifDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException


class TarifFragment : Fragment() {
    private var _binding: FragmentTarifBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var secilenGorsel : Uri? = null
    private var bitmap : Bitmap? = null
    private lateinit var dao : TarifDAO
    private lateinit var db: TarifDatabase
    private var secilenTarif : Tarif?= null
    private val mDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        db = Room.databaseBuilder(requireContext(),TarifDatabase::class.java,"Tarifler").build()
        dao = db.tarifDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTarifBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.setOnClickListener{
            gorselSec(it)
        }
        binding.buttonKaydet.setOnClickListener{
            kaydet(it)
        }
        binding.buttonSil.setOnClickListener{
            sil(it)
        }

        arguments?.let{
            val bilgi = TarifFragmentArgs.fromBundle(it).bilgi
            if(bilgi == "yeni"){
                binding.buttonSil.isEnabled = false
                binding.buttonKaydet.isEnabled = true
                binding.isimText.setText("")
                binding.malzemeText.setText("")

            }else{
                binding.buttonSil.isEnabled = true
                binding.buttonKaydet.isEnabled = false
                val id = TarifFragmentArgs.fromBundle(it).id
                mDisposable.add(
                    dao.findByID(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )
            }
        }
    }
    private fun handleResponse(tarif: Tarif){
        binding.isimText.setText(tarif.isim)
        binding.malzemeText.setText(tarif.malzeme)
        val bitmap = BitmapFactory.decodeByteArray(tarif.gorsel,0,tarif.gorsel.size)
        binding.imageView.setImageBitmap(bitmap)
        secilenTarif = tarif
        }

    fun kaydet(view : View){
        val isim = binding.isimText.text.toString()
        val malzeme = binding.malzemeText.text.toString()

        if(bitmap != null){
            val kucukBitmap = resmiKucult(bitmap!!,300)
            val outputStream = ByteArrayOutputStream()

            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()

            val tarif = Tarif(isim,malzeme,byteDizisi)

           mDisposable.add(
               dao.insert(tarif)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(this::handleResponse)
           )


        }
    }

    private fun handleResponse(){
        val action = TarifFragmentDirections.actionTarifFragmentToListeFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }
    fun sil(view : View){
        if(secilenTarif != null){
        mDisposable.add(
            dao.delete(tarif = secilenTarif!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
            )
        }
    }
    fun gorselSec(view: View) {
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireActivity().applicationContext,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                    ) {
                        Snackbar.make(
                            view,
                            "Permission needed for gallery",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("Give Permission",
                            View.OnClickListener {
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }).show()
                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)

                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireActivity().applicationContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        Snackbar.make(
                            view,
                            "Permission needed for gallery",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("Give Permission",
                            View.OnClickListener {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }).show()
                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                } else {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)

                }
            }
        }
    }
    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    secilenGorsel = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                requireActivity().contentResolver,
                                secilenGorsel!!
                            )
                            bitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(bitmap)
                        } else {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                secilenGorsel
                            )
                            binding.imageView.setImageBitmap(bitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(requireContext(), "Permisson needed!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun resmiKucult(bitmap: Bitmap, max: Int) : Bitmap{
        var width = bitmap.width
        var height = bitmap.height

        val bitmapOrani = width.toDouble() / height.toDouble()

        if(bitmapOrani > 1){
            // yatay
            width = max
            val kisaltilmisYukseklik = width / bitmapOrani
            height = kisaltilmisYukseklik.toInt()
        }else{
            // dikey
            height = max
            val kisaltilmisGenislik = height * bitmapOrani
            width = kisaltilmisGenislik.toInt()
        }
        return Bitmap.createScaledBitmap(bitmap,width,height,true)
    }
}