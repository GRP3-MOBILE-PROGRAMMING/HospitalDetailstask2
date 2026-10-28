package com.example.uhc.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestMode
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class ViewAccessAfterDestroyViewDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = ViewAccessAfterDestroyViewDetector()
    override fun getIssues(): List<Issue> = listOf(ViewAccessAfterDestroyViewDetector.ISSUE)

    fun testUnclearedViewFieldIsFlagged() {
        lint().files(
            *fragmentStubs,
            java(
                """
                package com.example;

                import android.widget.TextView;
                import androidx.fragment.app.Fragment;

                public class ListFragment extends Fragment {
                    private TextView title;
                    private String query;
                }
                """
            ).indented()
        ).testModes(TestMode.DEFAULT).run().expectErrorCount(1)
    }

    fun testFieldClearedInOnDestroyViewIsClean() {
        lint().files(
            *fragmentStubs,
            java(
                """
                package com.example;

                import android.widget.TextView;
                import androidx.fragment.app.Fragment;

                public class ListFragment extends Fragment {
                    private TextView title;

                    @Override
                    public void onDestroyView() {
                        super.onDestroyView();
                        title = null;
                    }
                }
                """
            ).indented()
        ).testModes(TestMode.DEFAULT).run().expectClean()
    }
}
