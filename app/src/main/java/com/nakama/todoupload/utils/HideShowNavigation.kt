package com.nakama.todoupload.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nakama.todoupload.R
import com.nakama.todoupload.ui.home.MainActivity

fun Fragment.hideBottomNavView() {
  val appBar: BottomAppBar = (activity as MainActivity).findViewById(R.id.bottomBar)
  val bottomNavView: BottomNavigationView = (activity as MainActivity).findViewById(R.id.bottomNavigationView)

  appBar.visibility = View.GONE
  bottomNavView.visibility = View.GONE
}

fun Fragment.showBottomNavView() {
  val appBar: BottomAppBar = (activity as MainActivity).findViewById(R.id.bottomBar)
  val bottomNavView: BottomNavigationView = (activity as MainActivity).findViewById(R.id.bottomNavigationView)

  appBar.visibility = View.VISIBLE
  bottomNavView.visibility = View.VISIBLE
}