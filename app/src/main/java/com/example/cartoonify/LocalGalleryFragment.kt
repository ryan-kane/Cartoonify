package com.example.cartoonify

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
const val ARG_RECENT_CREATE = "recent_create"
private const val TAG = "LocalGalleryFragment"
/**
 * A simple [Fragment] subclass.
 * Use the [LocalGalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocalGalleryFragment : Fragment(){
    // TODO: Rename and change types of parameters
    private var recentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recentPhotoPath = it.getString(ARG_RECENT_CREATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // show the creations from the directory that they are saved
        if(recentPhotoPath != null) {
            val recentPhotoUri = Uri.parse(recentPhotoPath)
            view.findViewById<ImageView>(R.id.image_recent_creation).setImageURI(recentPhotoUri)
        }

        val localGalleryGrid: GridView? = view.findViewById(R.id.local_gallery_grid_view)
        if (localGalleryGrid != null) {
            Log.d(TAG, "Creating Grid View")
            val imageFileList = requireActivity().fileList()
            Log.d(TAG,imageFileList.size.toString())
            localGalleryGrid.adapter = ImageAdapter(requireContext(), imageFileList)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param recent_photo_path Most Recent Photo Creation.
         * @return A new instance of fragment LocalGalleryFragment.
         */
        @JvmStatic
        fun newInstance(recent_photo_path: String?) =
            LocalGalleryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_RECENT_CREATE, recent_photo_path)
                }
            }
    }


    class ImageAdapter(): BaseAdapter() {

        lateinit var context: Context
        var imageList: Array<String>? = null

        constructor(context: Context, imageList: Array<String>): this() {
            this.context = context
            this.imageList = imageList
        }

        override fun getCount(): Int {
            if (imageList != null) {
                return imageList!!.size
            }
            return 0
        }

        override fun getItem(position: Int): Any? {
            if (imageList != null) {
                return imageList!![position]
            }
            return null

        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var imageView = ImageView(context)

            Log.d(TAG, imageList!![position])

            if(convertView != null) {
                imageView = convertView.findViewById(R.id.local_gallery_grid_view)
            }

            imageView.setImageURI(Uri.parse(imageList!![position]))
            return imageView
        }



    }
}

