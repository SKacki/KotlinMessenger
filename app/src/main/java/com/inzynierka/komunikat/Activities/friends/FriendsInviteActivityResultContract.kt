package com.inzynierka.komunikat.activities.friends

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.inzynierka.komunikat.classes.User

class FriendsInviteActivityResultContract : ActivityResultContract<String, User?>() {

    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, FriendsInviteActivity::class.java).apply {
            putExtra(SEARCH_PHRASE, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): User? {
        val invitedUser = intent?.getParcelableExtra(INVITED_USER) as? User

        return when {
            resultCode == Activity.RESULT_OK && invitedUser != null -> invitedUser
            else -> null
        }
    }

    companion object {
        private const val SEARCH_PHRASE = "SEARCH_PHRASE"
        private const val INVITED_USER = "INVITED USER"
    }
}