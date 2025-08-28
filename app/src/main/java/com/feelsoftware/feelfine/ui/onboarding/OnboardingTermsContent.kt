package com.feelsoftware.feelfine.ui.onboarding

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ahmer.pdfviewer.PDFView
import com.ahmer.pdfviewer.util.FitPolicy
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
internal fun OnboardingTermsContent(
    accepted: Boolean,
    onAcceptedChange: (accepted: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val termsDialogState = rememberTermsDialogState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Checkbox(
            checked = accepted,
            onCheckedChange = onAcceptedChange,
            modifier = Modifier
                .semantics {
                    contentDescription = "Accept Terms Of Use"
                }
        )

        val linkTag = "linkAnnotation"
        val annotatedText = buildAnnotatedString {
            val link = stringResource(R.string.onboarding_terms_link)
            val text = stringResource(R.string.onboarding_terms_text, link)

            val start = text.indexOf(link)
            val end = start + link.length

            append(text)
            addStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                ), start, end
            )
            addStringAnnotation(tag = linkTag, annotation = linkTag, start, end)
        }
        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(
                    tag = linkTag,
                    start = offset,
                    end = offset,
                ).firstOrNull()?.let {
                    termsDialogState.show()
                } ?: run {
                    // Reverse checkbox when clicked outside of link
                    onAcceptedChange(accepted.not())
                }
            },
            modifier = Modifier
                .semantics {
                    role = Role.Button
                    contentDescription = "Read Terms Of Use"
                }
        )
    }

    TermsDialog(termsDialogState = termsDialogState)
}

private class TermsDialogState {

    private val _openDialog = MutableStateFlow(false)
    val openDialog: StateFlow<Boolean> = _openDialog.asStateFlow()

    fun show() {
        _openDialog.value = true
    }

    fun hide() {
        _openDialog.value = false
    }
}

@Composable
private fun rememberTermsDialogState() = remember {
    TermsDialogState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TermsDialog(
    termsDialogState: TermsDialogState
) {
    val openDialog by termsDialogState.openDialog.collectAsStateWithLifecycle()
    if (openDialog.not()) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false },
    )

    ModalBottomSheet(
        onDismissRequest = { termsDialogState.hide() },
        sheetState = sheetState,
        dragHandle = null,
        contentWindowInsets = { WindowInsets() },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { termsDialogState.hide() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close Terms & Conditions"
                            )
                        }
                    },
                )
            }
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                factory = { context ->
                    PDFView(context, null).apply {
                        fromStream(resources.openRawResource(R.raw.terms_of_use))
                            .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                            .load()
                    }
                },
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingTermsContentPreview() {
    FeelFineTheme {
        OnboardingTermsContent(accepted = true, onAcceptedChange = {})
    }
}