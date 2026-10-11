package app.window.shop;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.BridgeWebViewClient;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWebView();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupWebView();
    }

    private void setupWebView() {
        Bridge currentBridge = getBridge();
        if (currentBridge != null) {
            WebView webView = currentBridge.getWebView();
            if (webView != null) {
                android.webkit.WebSettings settings = webView.getSettings();
                String userAgent = settings.getUserAgentString();
                if (userAgent != null) {
                    userAgent = userAgent.replace("; wv", "");
                    userAgent = userAgent.replaceAll("Version/[^ ]+\\s*", "");
                    settings.setUserAgentString(userAgent);
                }
                settings.setJavaScriptCanOpenWindowsAutomatically(true);
                settings.setDomStorageEnabled(true);
                settings.setDatabaseEnabled(true);

                webView.addJavascriptInterface(new Object() {
                    @android.webkit.JavascriptInterface
                    public void onLoadWebView() {
                        webView.post(new Runnable() {
                            @Override
                            public void run() {
                                String js = "if (typeof window.handleWebViewMessage === 'function') { " +
                                    "window.handleWebViewMessage({ " +
                                    "  upi_intent_data: ['com.google.android.apps.nbu.paisa.user', 'com.phonepe.app', 'net.one97.paytm', 'in.org.npci.upiapp', 'com.dreamplug.androidapp', 'com.whatsapp', 'in.amazon.mShop.android.shopping'] " +
                                    "}); " +
                                    "}";
                                webView.evaluateJavascript(js, null);
                            }
                        });
                    }
                }, "RazorpayWebViewBridge");
            }
            currentBridge.setWebViewClient(new BridgeWebViewClient(currentBridge) {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if (request != null && request.getUrl() != null) {
                        Uri url = request.getUrl();
                        String urlStr = url.toString();

                        if (urlStr.startsWith("upi://") || urlStr.startsWith("tez://") ||
                            urlStr.startsWith("phonepe://") || urlStr.startsWith("paytmmp://") ||
                            urlStr.startsWith("paytm://") || urlStr.startsWith("bhim://") ||
                            urlStr.startsWith("credpay://")) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                return true;
                            } catch (ActivityNotFoundException e) {
                                return true;
                            }
                        }

                        if (urlStr.startsWith("intent://")) {
                            try {
                                Intent intent = Intent.parseUri(urlStr, Intent.URI_INTENT_SCHEME);
                                if (intent != null) {
                                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                    intent.setComponent(null);
                                    intent.setSelector(null);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    try {
                                        startActivity(intent);
                                        return true;
                                    } catch (ActivityNotFoundException e) {
                                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                        if (fallbackUrl != null) {
                                            view.loadUrl(fallbackUrl);
                                            return true;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // Ignore malformed intent URIs
                            }
                        }
                    }
                    return super.shouldOverrideUrlLoading(view, request);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null) {
                        if (url.startsWith("upi://") || url.startsWith("tez://") ||
                            url.startsWith("phonepe://") || url.startsWith("paytmmp://") ||
                            url.startsWith("paytm://") || url.startsWith("bhim://") ||
                            url.startsWith("credpay://")) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                return true;
                            } catch (ActivityNotFoundException e) {
                                return true;
                            }
                        }

                        if (url.startsWith("intent://")) {
                            try {
                                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                                if (intent != null) {
                                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                    intent.setComponent(null);
                                    intent.setSelector(null);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    try {
                                        startActivity(intent);
                                        return true;
                                    } catch (ActivityNotFoundException e) {
                                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                        if (fallbackUrl != null) {
                                            view.loadUrl(fallbackUrl);
                                            return true;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // Ignore
                            }
                        }
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });
        }
    }
}
