@file:Suppress("unused")

package vector.app.ext.bind

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


fun <F : Fragment> FragmentActivity.bindFragment(id: Int)
        : ReadOnlyProperty<FragmentActivity, F> = required(id, fragFinder)

fun <F : Fragment> Fragment.bindFragment(id: Int)
        : ReadOnlyProperty<Fragment, F> = required(id, fragFinder)

@Suppress("UNCHECKED_CAST")
private fun <T, F : Fragment> required(id: Int, finder: T.(Int) -> Fragment?) =
    BindLazy { t: T, desc ->
        t.finder(id) as F? ?: fragNotFound(id, desc)
    }

private val FragmentActivity.fragFinder: FragmentActivity.(Int) -> Fragment?
    get() = { supportFragmentManager.findFragmentById(it) }
private val Fragment.fragFinder: Fragment.(Int) -> Fragment?
    get() = { childFragmentManager.findFragmentById(it) }

private fun fragNotFound(id: Int, desc: KProperty<*>): Nothing =
    throw IllegalStateException("Frag ID $id for '${desc.name}' not found.")
