package uk.q3c.krail.core.view

import uk.q3c.krail.core.navigate.NavigationState
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import java.io.Serializable

/**
 * Created by David Sowerby on 12 Jun 2018
 */
data class NavigationStateExt(val from: NavigationState?, val to: NavigationState, val node: UserSitemapNode) : Serializable