package se.qurit.myhealth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.researchstack.backbone.answerformat.TextAnswerFormat
import org.researchstack.backbone.model.ConsentDocument
import org.researchstack.backbone.model.ConsentSection
import org.researchstack.backbone.model.ConsentSignature
import org.researchstack.backbone.step.ConsentDocumentStep
import org.researchstack.backbone.step.ConsentVisualStep
import org.researchstack.backbone.step.QuestionStep
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.task.OrderedTask
import org.researchstack.backbone.task.Task
import org.researchstack.backbone.ui.ViewTaskActivity


class MainActivity : AppCompatActivity() {

    private val REQUEST_CONSENT = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val consentButton: Button = findViewById(R.id.consentButton) as Button

        consentButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                displayConsent()
            }
        })
    }

    private fun createConsentDocument(): ConsentDocument? {
        val document = ConsentDocument()
        document.setTitle("Demo Consent")
        document.signaturePageTitle = R.string.rsb_consent

        val sections: MutableList<ConsentSection?> = ArrayList()

        sections.add(
            createSection(
                ConsentSection.Type.Overview, "Overview Info", "<h1>Read " +
                        "This!</h1><p>Some " +
                        "really <strong>important</strong> information you should know about this step"
            )
        )
        sections.add(createSection(ConsentSection.Type.DataGathering, "Data Gathering Info", ""))
        sections.add(createSection(ConsentSection.Type.Privacy, "Privacy Info", ""))
        sections.add(createSection(ConsentSection.Type.DataUse, "Data Use Info", ""))
        sections.add(createSection(ConsentSection.Type.TimeCommitment, "Time Commitment Info", ""))
        sections.add(createSection(ConsentSection.Type.StudySurvey, "Study Survey Info", ""))
        sections.add(createSection(ConsentSection.Type.StudyTasks, "Study Task Info", ""))
        sections.add(
            createSection(
                ConsentSection.Type.Withdrawing, "Withdrawing Info", "Some detailed steps " +
                        "to withdrawal from this study. <ul><li>Step 1</li><li>Step 2</li></ul>"
            )
        )

        document.sections = sections

        val signature = ConsentSignature()
        signature.setRequiresName(true)
        signature.setRequiresSignatureImage(true)
        document.addSignature(signature)

        document.htmlReviewContent = "<div style=\"padding: 10px;\" class=\"header\">" +
                "<h1 style='text-align: center'>Review Consent!</h1></div>"

        return document
    }

    private fun createSection(
        type: ConsentSection.Type,
        summary: String,
        content: String
    ): ConsentSection? {
        val section = ConsentSection(type)
        section.summary = summary
        section.htmlContent = content
        return section
    }

    private fun createConsentSteps(document: ConsentDocument): List<Step>? {
        val steps: MutableList<Step> = ArrayList()
        for (section in document.sections) {
            val visualStep = ConsentVisualStep(section.type.toString())
            visualStep.section = section
            visualStep.nextButtonString = getString(R.string.rsb_next)
            steps.add(visualStep)
        }

        val documentStep = ConsentDocumentStep("consent_doc")
        documentStep.consentHTML = document.htmlReviewContent
        documentStep.confirmMessage = getString(R.string.rsb_consent_review_reason)

        steps.add(documentStep)

        val signature = document.getSignature(0)

        if (signature.requiresName()) {
            val format = TextAnswerFormat()
            format.setIsMultipleLines(false)
            val fullName = QuestionStep(
                "consent_name_step", "Please enter your full name",
                format
            )
            fullName.placeholder = "Full name"
            fullName.isOptional = false
            steps.add(fullName)
        }

        return steps
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun displayConsent() {
        // 1
        // 1
        val document = createConsentDocument()

// 2
        // 2
        val steps =
            createConsentSteps(document!!)

// 3
        // 3
        val consentTask: Task = OrderedTask("consent_task", steps)

// 4
        // 4
        val intent = ViewTaskActivity.newIntent(this, consentTask)
        startActivityForResult(intent, REQUEST_CONSENT)

    }


}
