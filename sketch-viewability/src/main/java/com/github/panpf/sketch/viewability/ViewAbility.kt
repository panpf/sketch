package com.github.panpf.sketch.viewability

/**
 * ViewAbility represents the extension function of a View.
 *
 * After defining your ViewAbility, attach the ViewAbility to any View that implements the ViewAbilityContainer
 * interface through the addViewAbility() method of ViewAbilityContainer.
 *
 * You can also implement the ViewObserver interface to listen to various events of View and observe various properties to help implement functions
 */
interface ViewAbility {

    /**
     * Attached host. Not null means running
     */
    var host: Host?
}