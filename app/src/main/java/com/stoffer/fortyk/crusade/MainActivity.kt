package com.stoffer.fortyk.crusade

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stoffer.fortyk.crusade.domain.Force
import com.stoffer.fortyk.crusade.domain.Unit
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson

/**
 * TODO: ADDITIONAL UI UPDATES (Accent colors)
 * ca-app-pub-3940256099942544/6300978111 == test ad view
 */

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var infoButton: ImageButton
    private lateinit var themeButton: ImageButton

    override fun onResume() {
        super.onResume()

        setContentView(R.layout.activity_main)

        // init ads
        MobileAds.initialize(this)

        val existingForces = getExistingForces()
        val adapterForces = existingForces.sortedBy { it.name }.toTypedArray()
        // setup recycler view
        viewManager = LinearLayoutManager(this)
        viewAdapter = ForceAdapter(adapterForces)
        recyclerView = findViewById<RecyclerView>(R.id.forceRecycler).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

        // info button
        infoButton = findViewById(R.id.btnInfo)
        infoButton.setOnClickListener {
            showInfo()
        }

        // theme button
        themeButton = findViewById(R.id.btnTheme)
        themeButton.setOnClickListener {
            pickTheme()
        }


    }

    /**
     * Read the existing forces stored in shared prefs.
     */
    private fun getExistingForces(): List<Force> {
        val existingForces = mutableListOf<Force>()
        val prefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val gson = Gson()
        val nameSet = prefs.getStringSet("forceIds", null)
        nameSet?.forEach {
            val storedForceJson = prefs.getString(it, "")
            val force = gson.fromJson(storedForceJson, Force::class.java)
            existingForces.add(force)
        }
        return existingForces
    }

    /**
     * Start the force activity to add a new force.
     */
    fun addForce(view: View) {
        val forceIntent = Intent(this, ForceAcitivity::class.java)
        val gson = Gson()
        val newForceJson = gson.toJson(Force(units = mutableSetOf()))
        forceIntent.putExtra("force", newForceJson)

        startActivity(forceIntent)
    }

    /**
     * Override the onBackPressed method to go back to the home screen.
     */
    override fun onBackPressed() {
        val home = Intent(Intent.ACTION_MAIN)
        home.addCategory(Intent.CATEGORY_HOME)
        home.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(home)
    }

    /**
     * Show the application info screen.
     */
    private fun showInfo() {
        val builder: AlertDialog.Builder? = this.let {
            AlertDialog.Builder(it)
        }
        val dialogTitleView = TextView(this)
        dialogTitleView.text = "Crusade Tracker"
        dialogTitleView.textSize = 20F
        dialogTitleView.gravity = Gravity.CENTER_HORIZONTAL
        val dialogTextView = TextView(this)
        dialogTextView.gravity = Gravity.CENTER_HORIZONTAL
        var dialogMessage = "This is application was designed to track your Crusade Forces throughout their Crusade Campaigns.\n\n\n"
        dialogMessage += "- Click the circular Add button on the Crusade Forces screen to add a new force.\n\n"
        dialogMessage += "- Fill in the info for your Crusade Force and click the Add Unit button to add Units to the Force.\n\n"
        dialogMessage += "- Click the info button to show this informational screen.\n\n"
//        dialogMessage += "- Click the palette button to show the accent color selection screen.\n\n"
        dialogMessage += "This application is completely unofficial and is in no way associated with or endorsed by Games Workshop Limited. \n"
        dialogTextView.text = dialogMessage
        builder?.setCustomTitle(dialogTitleView)
        builder?.setView(dialogTextView)
        val infoDialog: AlertDialog? = builder?.create()
        infoDialog?.show()
    }

    // TODO: theme
    private fun pickTheme() {
        val builder: AlertDialog.Builder? = this.let {
            AlertDialog.Builder(it)
        }
        val dialogTitleView = TextView(this)
        dialogTitleView.text = "Coming Soon!"
        dialogTitleView.textSize = 20F
        dialogTitleView.gravity = Gravity.CENTER_HORIZONTAL
        builder?.setCustomTitle(dialogTitleView)
        val themeDialog: AlertDialog? = builder?.create()
        themeDialog?.show()
    }

    private fun bootstrap() {
        // test units and forces
        // TODO: actual units

        val force1 = Force(
            name = "The BlightBorne",
            faction = "Death Guard",
            playerName = "test",
            tally = 1,
            battlesWon = 1,
            requisitionPoints = 1,
            supplyLimit = 5,
            supplyUsed = 5,
            goalsInfoAndVictories = null,

            units = mutableSetOf()
        )

        val unit = Unit(
            name = "testUnit",
            force_id = force1.id
        )
        force1.units.add(unit)


        val force2 = Force(
            name = "Baal's Angels",
            faction = "Blood Angels",
            playerName = "test",
            tally = 1,
            battlesWon = 1,
            requisitionPoints = 1,
            supplyLimit = 5,
            supplyUsed = 5,
            goalsInfoAndVictories = null,

            units = mutableSetOf()
        )
        val unit2 = Unit(
            name = "testUnit2",
            force_id = force2.id
        )
        force2.units.add(unit2)

        val force3 = Force(
            name = "Soldiers of Silence",
            faction = "Nercons",
            units = mutableSetOf()
        )

        val unit3 = Unit(
            name = "testUnit3",
            force_id = force3.id
        )

        force3.units.add(unit3)

        // setup prefs, editor, and gson.
        val prefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()

        // wipe any existing data****
        editor.clear()

        // store the list of forces (force pref names)
        editor.putStringSet("forceIds", mutableSetOf(force1.id, force2.id, force3.id))

        // write to shared prefs
        val force1Json = gson.toJson(force1)
        editor.putString(force1.id, force1Json)
        editor.apply()

        val force2Json = gson.toJson(force2)
        editor.putString(force2.id, force2Json)
        editor.apply()

        val force3Json = gson.toJson(force3)
        editor.putString(force3.id, force3Json)
        editor.apply()

        val retrievedForcesList = mutableListOf<Force>()

        // read the set of force names
        val forceIds = prefs.getStringSet("forceIds", null)

        // loop through those names and read the json stored in shared prefs
        // for each
        forceIds?.forEach {
            val storedForceJson = prefs.getString(it, "")
            val force = gson.fromJson(storedForceJson, Force::class.java)
            retrievedForcesList.add(force)
        }

        for (force in retrievedForcesList) {
            Log.d("BOOTSTRAP-FORCE", force.name.toString())
            Log.d("BOOTSTRAP-FORCE", force.id)
            Log.d("BOOTSTRAP-FORCE", force.units.elementAt(0).name.toString())
        }
    }
}