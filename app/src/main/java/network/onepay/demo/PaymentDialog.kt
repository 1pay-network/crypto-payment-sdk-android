package network.onepay.demo

import android.app.Dialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.net.URLEncoder

class PaymentDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {}.apply {
            setOnShowListener { dialog ->
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    ?.also {
                        it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                        with(BottomSheetBehavior.from(it)) {
                            peekHeight = Resources.getSystem().displayMetrics.heightPixels
                            isHideable = false
                        }
                        it.requestLayout()
                    }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webviewPayment = view.findViewById<WebView>(R.id.webview_payment)
        with(webviewPayment) {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    // Override url loading for handle connect wallet
                    return handlePaymentResult(request.url)
                }

                override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    // Handle for payment result
                    handlePaymentResult(Uri.parse(url))
                }

            }
        }
        webviewPayment.loadUrl(buildPaymentURL(0.1f, "usdt", "test note"))
    }


    /**
     * Build payment url for payment page
     *
     * @param amount: amount to pay
     * @param token: token name lowercase
     * @param note: note for this payment
     * @return string url for payment page
     */
    private fun buildPaymentURL(amount: Float, token: String, note: String): String {
        val encodedNote = URLEncoder.encode(note, "utf8")
        return "https://1pay.network/app" +
                "?recipient=${Constants.RECIPIENT}&network=${Constants.NETWORK}&token=${Constants.TOKENS}" +
                "&paymentAmount=${amount}&paymentToken=${token}&paymentNote=${encodedNote}"
    }


    /**
     * Handle payment result and deeplink when connect to wallet
     * For more information about return data, please visit 1pay documentations
     *
     * @param uri
     * @return true if you want handle yourself and skip webview default behavior, otherwise false
     */
    private fun handlePaymentResult(uri: Uri): Boolean {
        val url = uri.toString()
        // Check url is from 1pay.network
        if (url.contains("1pay.network")) {
            // Handle success
            if (url.contains("1pay.network/success")) {
                // Get data from url.
                val hash = uri.getQueryParameter("hash") ?: "no hash"
                Toast.makeText(requireContext(), "Success. Hash = $hash", Toast.LENGTH_SHORT).show()
            } else if (url.contains("1pay.network/fail")) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            try {
                // Handle wallet connect, open wallet client app or open playstore to install it
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (_: Exception) {

            }
            return true
        }
        return false
    }
}