package com.example.cartoonify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

import kotlinx.android.synthetic.main.activity_menu.*

private const val CREATE = 1
private const val TAG = "MenuActivity"

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        setSupportActionBar(toolbar)

        val fragMan = supportFragmentManager
        val fragTrans = fragMan.beginTransaction()

        val localGalleryFragment = LocalGalleryFragment.newInstance(null)

        fragTrans.add(R.id.gallery_fragment_container, localGalleryFragment)

        fragTrans.commit()

        fab.setOnClickListener { _ ->
            val intent = Intent(this, CreateActivity::class.java)
            startActivityForResult(intent, CREATE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                CREATE -> {
                    // show the most recent creation

                }
            }
        }

    }

}
