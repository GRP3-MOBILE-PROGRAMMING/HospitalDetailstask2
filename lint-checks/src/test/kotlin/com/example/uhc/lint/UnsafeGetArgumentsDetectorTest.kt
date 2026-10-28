package com.example.uhc.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestMode
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class UnsafeGetArgumentsDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = UnsafeGetArgumentsDetector()
    override fun getIssues(): List<Issue> = listOf(UnsafeGetArgumentsDetector.ISSUE)

    fun testDirectDereferenceIsFlagged() {
        lint().files(
            *fragmentStubs,
            java(
                """
                package com.example;

                import androidx.fragment.app.Fragment;

                public class DetailFragment extends Fragment {
                    String name() {
                        return getArguments().getString("name");
                    }
                }
                """
            ).indented()
        ).testModes(TestMode.DEFAULT).run().expectErrorCount(1)
    }

    fun testNullCheckedArgumentsAreClean() {
        lint().files(
            *fragmentStubs,
            java(
                """
                package com.example;

                import android.os.Bundle;
                import androidx.fragment.app.Fragment;

                public class DetailFragment extends Fragment {
                    String name() {
                        Bundle args = getArguments();
                        if (args == null) return "";
                        return args.getString("name") + requireArguments().getString("id");
                    }
                }
                """
            ).indented()
        ).testModes(TestMode.DEFAULT).run().expectClean()
    }
}
