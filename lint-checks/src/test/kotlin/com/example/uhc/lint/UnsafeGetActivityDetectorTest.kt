package com.example.uhc.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestMode
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class UnsafeGetActivityDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = UnsafeGetActivityDetector()
    override fun getIssues(): List<Issue> = listOf(UnsafeGetActivityDetector.ISSUE)

    fun testDirectDereferenceIsFlagged() {
        lint().files(
            *fragmentStubs,
            java(
                """
                package com.example;

                import androidx.fragment.app.Fragment;

                public class DetailFragment extends Fragment {
                    void show() {
                        getActivity().setTitle("Patient");
                        getContext().getPackageName();
                    }
                }
                """
            ).indented()
        ).testModes(TestMode.DEFAULT).run().expectErrorCount(2)
    }

    fun testNullCheckedAndRequireAreClean() {
        lint().files(
            *fragmentStubs,
            java(
                """
                package com.example;

                import android.app.Activity;
                import androidx.fragment.app.Fragment;

                public class DetailFragment extends Fragment {
                    void show() {
                        Activity activity = getActivity();
                        if (activity != null) activity.setTitle("Patient");
                        requireContext().getPackageName();
                    }
                }
                """
            ).indented()
        ).testModes(TestMode.DEFAULT).run().expectClean()
    }
}
