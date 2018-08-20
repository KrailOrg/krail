package uk.q3c.krail.core.form

import com.google.inject.Inject
import com.vaadin.data.provider.ListDataProvider
import uk.q3c.krail.core.i18n.CommonLabelKey
import uk.q3c.krail.i18n.Translate


class YesNoDataProvider @Inject constructor(val translate: Translate) : ListDataProvider<String>(setOf(CommonLabelKey.No, CommonLabelKey.Yes).map { k -> translate.from(k) }.toList())
class BeforeAfterDataProvider @Inject constructor(val translate: Translate) : ListDataProvider<String>(setOf(CommonLabelKey.Before, CommonLabelKey.After).map { k -> translate.from(k) }.toList())


