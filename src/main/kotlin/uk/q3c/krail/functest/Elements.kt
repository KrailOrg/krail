package uk.q3c.krail.functest

/**
 * Created by David Sowerby on 23 Jan 2018
 */


interface BaseElement {
    val id: String
    //    val icon: Resource?
    fun captionShouldBe(expectedCaption: String)
//    fun descriptionShouldBe(expectedDescription: String)
    //    fun localeShouldBe(expectedLocale: Locale)
//    fun primaryStyleNameShouldBe(expectedPrimaryStyleName: String)
//    fun styleNameShouldBe(expectedStyleName: String)
//    fun shouldBeEnabled()
//    fun shouldNotBeEnabled()
//    fun shouldBeVisible()
//    fun shouldNotBeVisible()

    // may need others from Focusable
}

interface ValueElement<in T> {
    //    fun requiredIndicatorShouldBeVisible()
//    fun requiredIndicatorShouldNotBeVisible()
    fun valueShouldBe(expectedValue: T)

    fun setValue(value: T)
}

interface LabelElement : BaseElement {
    fun valueShouldBe(expectedValue: String)
}

interface CheckBoxElement : BaseElement, ValueElement<Boolean>

interface ButtonElement : BaseElement {
    fun click()
}

interface GridElement : BaseElement
interface TreeGridElement : BaseElement
interface MenuElement : BaseElement {
    fun select(path: String)
}


interface TextFieldElement : BaseElement, ValueElement<String>

interface TextAreaElement : BaseElement, ValueElement<String>

interface ViewElement {
    val id: String
}

interface PageElement {
    val id: String
}


