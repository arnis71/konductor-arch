package com.arnis.arch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arnis.konductor.Controller
import com.arnis.konductor.ControllerChangeHandler
import com.arnis.konductor.RouterTransaction
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.UI

/** Created by arnis on 07/12/2017 */

abstract class ViewKontroller<out T: Abstraction>(val abstraction: T, withParams: (Bundle.() -> Bundle)? = null) : Controller(withParams?.let { Bundle().let(it) }) {
    abstract val layout: AnkoContext<Context>.() -> Unit

    fun routeTo(kontroller: ViewKontroller<*>,
                overridePop: ControllerChangeHandler? = null,
                overridePush: ControllerChangeHandler? = null)
            = (activity as KonductorActivity).changeHandler.let {
        router.pushController(RouterTransaction.with(kontroller)
                .popChangeHandler(overridePop ?: it.popHandler)
                .pushChangeHandler(overridePush?: it.pushHandler))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View  {
        return container.context.UI(layout).view
    }

    override fun onDestroy() = abstraction.clear()
}

inline fun <reified T> ViewKontroller<*>.bind(updateOnAttach: Boolean = true,
                                           noinline update: (data: T) -> Unit): KontrollerDataFlow<T> {
    return abstraction.get<T>().also {
        it.updateOnAttach = updateOnAttach
        it.onFlow(update)
        it.bindTo(this)
    }
}