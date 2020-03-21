package dev.bonch.herehackpurify.fragments.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import dev.bonch.herehackpurify.R
import dev.bonch.herehackpurify.activities.RegistrationActivity

class SMSDialogFragment : DialogFragment() {

    private lateinit var eSmsCode: EditText
    private lateinit var bSendCode: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_sms, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eSmsCode = view.findViewById(R.id.sms_code_et)
        bSendCode = view.findViewById(R.id.code_send_btn)

        bSendCode.setOnClickListener {
            val ft: FragmentTransaction? = fragmentManager?.beginTransaction()
            val fragmentToRemove: Fragment? = fragmentManager?.findFragmentByTag("sms_code")
            if (fragmentToRemove != null) {
                ft?.remove(fragmentToRemove)
            }
            ft?.addToBackStack(null)
            ft?.commit()

            val intent = Intent(context, RegistrationActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        }
    }
}