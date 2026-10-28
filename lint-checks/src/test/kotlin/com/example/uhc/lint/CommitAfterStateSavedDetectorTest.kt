package com.example.uhc.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestMode
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class CommitAfterStateSavedDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = CommitAfterStateSavedDetector()
    override fun getIssues(): List<Issue> = listOf(CommitAfterStateSavedDetector.ISSUE)

    fun testCommitInOnStopAndUnguardedCallbackIsFlagged() {
        lint().files(
            *fragmentStubs,
            java(
                """
                package com.example;

                import android.os.Handler;
                import androidx.fragment.app.Fragment;
                import androidx.fragment.app.FragmentActivity;

                public class MainActivity extends FragmentActivity {
                    @Override
                    protected void onStop() {
                        super.onStop();
                        getSupportFragmentManager().beginTransaction().replace(1, new Fragment()).commit();
                    }

                    void showLater(Handler handler) {
                        handler.postDelayed(() ->
                            getSupportFragmentManager().beginTransaction().replace(1, new Fragment()).commit(), 500);
                    }
                }
                """
            ).indented()
        ).testModes(TestMode.DEFAULT).run().expectErrorCount(2)
    }

    fun testCommitInOnCreateOrGuardedCallbackIsClean() {
        lint().files(
            *fragmentStubs,
            java(
                """
                package com.example;

                import android.os.Bundle;
                import android.os.Handler;
                import androidx.fragment.app.Fragment;
                import androidx.fragment.app.FragmentActivity;

                public class MainActivity extends FragmentActivity {
                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        getSupportFragmentManager().beginTransaction().replace(1, new Fragment()).commit();
                    }

                    void showLater(Handler handler) {
                        handler.postDelayed(() -> {
                            if (getSupportFragmentManager().isStateSaved()) return;
                            getSupportFragmentManager().beginTransaction().replace(1, new Fragment()).commit();
                        }, 500);
                    }
                }
                """
            ).indented()
        ).testModes(TestMode.DEFAULT).run().expectClean()
    }
}
