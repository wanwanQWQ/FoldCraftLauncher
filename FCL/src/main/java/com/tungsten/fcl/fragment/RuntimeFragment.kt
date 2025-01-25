package com.tungsten.fcl.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources
import com.tungsten.fcl.FCLApplication
import com.tungsten.fcl.R
import com.tungsten.fcl.activity.SplashActivity
import com.tungsten.fcl.databinding.FragmentRuntimeBinding
import com.tungsten.fcl.util.ReadTools
import com.tungsten.fcl.util.RuntimeUtils
import com.tungsten.fclauncher.utils.FCLPath
import com.tungsten.fclcore.task.Schedulers
import com.tungsten.fclcore.util.io.FileUtils
import com.tungsten.fclcore.util.Logging
import com.tungsten.fcllibrary.component.FCLFragment
import com.tungsten.fcllibrary.component.theme.Theme
import com.tungsten.fcllibrary.component.theme.ThemeEngine
import com.tungsten.fcllibrary.util.LocaleUtils
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.logging.Level

class RuntimeFragment : FCLFragment(), View.OnClickListener {
    private lateinit var bind: FragmentRuntimeBinding
    var lwjgl: Boolean = false
    var cacio: Boolean = false
    var cacio11: Boolean = false
    var cacio17: Boolean = false
    var java8: Boolean = false
    var java11: Boolean = false
    var java17: Boolean = false
    var java21: Boolean = false
    var jna: Boolean = false
    var gameResource: Boolean = false
    var others: Boolean = false
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("launcher", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val view = inflater.inflate(R.layout.fragment_runtime, container, false)
        bind = FragmentRuntimeBinding.bind(view)
        bind.install.setOnClickListener(this)
        Schedulers.defaultScheduler().execute {
            initState()
            Schedulers.androidUIThread().execute {
                setColorByTag(view, "runtime_text", ThemeEngine.getInstance().getTheme().getColor2())
                refreshDrawables()
                check()
            }
        }
        return view
    }

    private fun setColorByTag(view: View, tag: String, color: Int) {
        if (view is TextView && view.tag == tag) {
            view.setTextColor(color)
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setColorByTag(view.getChildAt(i), tag, color)
            }
        }
    }

    private fun initState() {
        try {
            lwjgl = RuntimeUtils.isLatest(
                FCLPath.LWJGL_DIR,
                "/assets/app_runtime/lwjgl"
            ) && RuntimeUtils.isLatest(
                FCLPath.LWJGL_DIR + "-boat",
                "/assets/app_runtime/lwjgl-boat"
            )
            cacio = RuntimeUtils.isLatest(
                FCLPath.CACIOCAVALLO_8_DIR,
                "/assets/app_runtime/caciocavallo"
            )
            cacio11 = RuntimeUtils.isLatest(
                FCLPath.CACIOCAVALLO_11_DIR,
                "/assets/app_runtime/caciocavallo11"
            )
            cacio17 = RuntimeUtils.isLatest(
                FCLPath.CACIOCAVALLO_17_DIR,
                "/assets/app_runtime/caciocavallo17"
            )
            java8 = RuntimeUtils.isLatest(FCLPath.JAVA_8_PATH, "/assets/app_runtime/java/jre8")
            java11 = RuntimeUtils.isLatest(FCLPath.JAVA_11_PATH, "/assets/app_runtime/java/jre11")
            java17 = RuntimeUtils.isLatest(FCLPath.JAVA_17_PATH, "/assets/app_runtime/java/jre17")
            java21 = RuntimeUtils.isLatest(FCLPath.JAVA_21_PATH, "/assets/app_runtime/java/jre21")
            jna = RuntimeUtils.isLatest(FCLPath.JNA_PATH, "/assets/app_runtime/jna")
            gameResource = RuntimeUtils.isLatest(FCLPath.SHARED_COMMON_DIR, "/assets/.minecraft")
            others = RuntimeUtils.isLatest(FCLPath.INTERNAL_DIR, "/assets/othersInternal")
        } catch (e: IOException) {
            Logging.LOG.log(Level.SEVERE, "Unable to check runtime version", e)
        }
    }

    private fun refreshDrawables() {
        if (context != null) {
            val stateUpdate =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_update_24)
            val stateDone =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_done_24)

            stateUpdate?.setTint(Color.RED)
            stateDone?.setTint(Color.GREEN)

            bind.apply {
                lwjglState.setBackgroundDrawable(if (lwjgl) stateDone else stateUpdate)
                cacioState.setBackgroundDrawable(if (cacio) stateDone else stateUpdate)
                cacio11State.setBackgroundDrawable(if (cacio11) stateDone else stateUpdate)
                cacio17State.setBackgroundDrawable(if (cacio17) stateDone else stateUpdate)
                java8State.setBackgroundDrawable(if (java8) stateDone else stateUpdate)
                java11State.setBackgroundDrawable(if (java11) stateDone else stateUpdate)
                java17State.setBackgroundDrawable(if (java17) stateDone else stateUpdate)
                java21State.setBackgroundDrawable(if (java21) stateDone else stateUpdate)
                jnaState.setBackgroundDrawable(if (jna) stateDone else stateUpdate)
                gameResourceState.setBackgroundDrawable(if (gameResource) stateDone else stateUpdate)
                othersState.setBackgroundDrawable(if (others) stateDone else stateUpdate)
            }
        }
    }

    private val isLatest: Boolean
        get() = lwjgl && cacio && cacio11 && cacio17 && java8 && java11 && java17 && java21 && jna && gameResource

    private fun check() {
        if (!isLatest) return
        if (others) {
            if (!installing) {
                (activity as SplashActivity).enterLauncher()
            } else {
                (activity as SplashActivity).finish()
                System.exit(0)
            }
        } else if (installing) {
            checkOthers()
        }
    }

    private var installingOthers = false
    private fun checkOthers() {
        if (installingOthers) return
        installingOthers = true
        bind.apply {
            if (!others) {
                othersProgress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.reloadConfiguration(context)
                        RuntimeUtils.copyAssetsDirToLocalDir(context, "othersExternal", FCLPath.EXTERNAL_DIR)
                        RuntimeUtils.copyAssetsDirToLocalDir(context, "othersInternal", FCLPath.INTERNAL_DIR)
                        others = true
                        activity?.runOnUiThread {
                            othersState.visibility = View.VISIBLE
                            othersProgress.visibility = View.GONE
                            refreshDrawables()
                            check()
                        }
                    } catch (e: Exception) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install other resource", e)
                    }
                }.start()
            }
        }
    }

    private var installing = false
    private fun install() {
        if (installing) return
        installing = true
        bind.apply {
            if (!gameResource) {
                gameResourceState.visibility = View.GONE
                gameResourceProgress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.deleteOldFiles(context)
                        RuntimeUtils.copyAssetsDirToLocalDir(context, ".minecraft", FCLPath.SHARED_COMMON_DIR)
                        RuntimeUtils.copyAssetsDirToLocalDir(context, "minecraft", FCLPath.EXTERNAL_DIR + "/minecraft")
                        gameResource = true
                        activity?.runOnUiThread {
                            gameResourceState.visibility = View.VISIBLE
                            gameResourceProgress.visibility = View.GONE
                            refreshDrawables()
                            check()
                        }
                    }catch (e: Exception) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install game resource", e)
                    }
                }.start()
            }
            if (!lwjgl) {
                lwjglState.visibility = View.GONE
                lwjglProgress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.install(context, FCLPath.LWJGL_DIR, "app_runtime/lwjgl")
                        RuntimeUtils.install(
                            context,
                            FCLPath.LWJGL_DIR + "-boat",
                            "app_runtime/lwjgl-boat"
                        )
                        lwjgl = true
                    } catch (e: IOException) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install lwjgl", e)
                    }
                    activity?.runOnUiThread {
                        lwjglState.visibility = View.VISIBLE
                        lwjglProgress.visibility = View.GONE
                        refreshDrawables()
                        check()
                    }
                }.start()
            }
            if (!cacio) {
                cacioState.visibility = View.GONE
                cacioProgress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.install(
                            context,
                            FCLPath.CACIOCAVALLO_8_DIR,
                            "app_runtime/caciocavallo"
                        )
                        cacio = true
                    } catch (e: IOException) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install caciocavallo", e)
                    }
                    activity?.runOnUiThread {
                        cacioState.visibility = View.VISIBLE
                        cacioProgress.visibility = View.GONE
                        refreshDrawables()
                        check()
                    }
                }.start()
            }
            if (!cacio11) {
                cacio11State.visibility = View.GONE
                cacio11Progress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.install(
                            context,
                            FCLPath.CACIOCAVALLO_11_DIR,
                            "app_runtime/caciocavallo11"
                        )
                        cacio11 = true
                    } catch (e: IOException) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install caciocavallo11", e)
                    }
                    activity?.runOnUiThread {
                        cacio11State.visibility = View.VISIBLE
                        cacio11Progress.visibility = View.GONE
                        refreshDrawables()
                        check()
                    }
                }.start()
            }
            if (!cacio17) {
                cacio17State.visibility = View.GONE
                cacio17Progress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.install(
                            context,
                            FCLPath.CACIOCAVALLO_17_DIR,
                            "app_runtime/caciocavallo17"
                        )
                        cacio17 = true
                    } catch (e: IOException) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install caciocavallo17", e)
                    }
                    activity?.runOnUiThread {
                        cacio17State.visibility = View.VISIBLE
                        cacio17Progress.visibility = View.GONE
                        refreshDrawables()
                        check()
                    }
                }.start()
            }
            if (!java8) {
                java8State.visibility = View.GONE
                java8Progress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.installJava(
                            context,
                            FCLPath.JAVA_8_PATH,
                            "app_runtime/java/jre8"
                        )
                        java8 = true
                    } catch (e: IOException) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install java8", e)
                    }
                    activity?.runOnUiThread {
                        java8State.visibility = View.VISIBLE
                        java8Progress.visibility = View.GONE
                        refreshDrawables()
                        check()
                    }
                }.start()
            }
            if (!java11) {
                java11State.visibility = View.GONE
                java11Progress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.installJava(
                            context,
                            FCLPath.JAVA_11_PATH,
                            "app_runtime/java/jre11"
                        )
                        java11 = true
                    } catch (e: IOException) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install java11", e)
                    }
                    activity?.runOnUiThread {
                        java11State.visibility = View.VISIBLE
                        java11Progress.visibility = View.GONE
                        refreshDrawables()
                        check()
                    }
                }.start()
            }
            if (!java17) {
                java17State.visibility = View.GONE
                java17Progress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.installJava(
                            context,
                            FCLPath.JAVA_17_PATH,
                            "app_runtime/java/jre17"
                        )
                        java17 = true
                    } catch (e: IOException) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install java17", e)
                    }
                    activity?.runOnUiThread {
                        java17State.visibility = View.VISIBLE
                        java17Progress.visibility = View.GONE
                        refreshDrawables()
                        check()
                    }
                }.start()
            }
            if (!java21) {
                java21State.visibility = View.GONE
                java21Progress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.installJava(
                            context,
                            FCLPath.JAVA_21_PATH,
                            "app_runtime/java/jre21"
                        )
                        java21 = true
                    } catch (e: IOException) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install java21", e)
                    }
                    activity?.runOnUiThread {
                        java21State.visibility = View.VISIBLE
                        java21Progress.visibility = View.GONE
                        refreshDrawables()
                        check()
                    }
                }.start()
            }
            if (!jna) {
                jnaState.visibility = View.GONE
                jnaProgress.visibility = View.VISIBLE
                Thread {
                    try {
                        RuntimeUtils.installJna(
                            context,
                            FCLPath.JNA_PATH,
                            "app_runtime/jna"
                        )
                        jna = true
                    } catch (e: IOException) {
                        Logging.LOG.log(Level.SEVERE, "Failed to install jna", e)
                    }
                    activity?.runOnUiThread {
                        jnaState.visibility = View.VISIBLE
                        jnaProgress.visibility = View.GONE
                        refreshDrawables()
                        check()
                    }
                }.start()
            }
            if (!others) {
                othersState.visibility = View.GONE
                check()
            }
        }
    }

    override fun onClick(view: View) {
        if (view === bind.install) {
            install()
        }
    }
}
