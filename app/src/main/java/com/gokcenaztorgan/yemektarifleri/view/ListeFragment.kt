package com.gokcenaztorgan.yemektarifleri.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.gokcenaztorgan.yemektarifleri.adapter.TarifAdapter
import com.gokcenaztorgan.yemektarifleri.databinding.FragmentListeBinding
import com.gokcenaztorgan.yemektarifleri.model.Tarif
import com.gokcenaztorgan.yemektarifleri.roomdb.TarifDAO
import com.gokcenaztorgan.yemektarifleri.roomdb.TarifDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListeFragment : Fragment() {

    private var _binding: FragmentListeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dao : TarifDAO
    private lateinit var db: TarifDatabase
    private val mDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(requireContext(),TarifDatabase::class.java,"Tarifler").build()
        dao = db.tarifDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListeBinding.inflate(inflater, container, false)
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
        binding.floatingActionButton2.setOnClickListener{
            yeniEkle(it)
        }
        binding.tarifRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        veriAl()
    }

    private fun veriAl(){
        mDisposable.add(
            dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(tarifler : List<Tarif>){
        tarifler.forEach{
            val adapter = TarifAdapter(tarifler)
            binding.tarifRecyclerView.adapter = adapter
        }


    }

    fun yeniEkle(view : View){
        val action = ListeFragmentDirections.actionListeFragmentToTarifFragment("yeni",0)
        Navigation.findNavController(view).navigate(action)
    }
}