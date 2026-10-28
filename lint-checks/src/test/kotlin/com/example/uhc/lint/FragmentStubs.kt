package com.example.uhc.lint

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.java

/** Minimal androidx.fragment API so detector tests don't need the real AAR. */
internal val fragmentStubs: Array<TestFile> = arrayOf(
    java(
        """
        package androidx.fragment.app;

        import android.content.Context;
        import android.os.Bundle;

        public class Fragment {
            public FragmentActivity getActivity() { return null; }
            public Context getContext() { return null; }
            public Bundle getArguments() { return null; }
            public FragmentActivity requireActivity() { return null; }
            public Context requireContext() { return null; }
            public Bundle requireArguments() { return null; }
            public FragmentManager getParentFragmentManager() { return null; }
            public boolean isStateSaved() { return false; }
            public void onDestroyView() { }
            public void onStop() { }
            public void onSaveInstanceState(Bundle outState) { }
        }
        """
    ).indented(),
    java(
        """
        package androidx.fragment.app;

        public class FragmentActivity extends android.app.Activity {
            public FragmentManager getSupportFragmentManager() { return null; }
        }
        """
    ).indented(),
    java(
        """
        package androidx.fragment.app;

        public abstract class FragmentManager {
            public abstract FragmentTransaction beginTransaction();
            public abstract boolean isStateSaved();
        }
        """
    ).indented(),
    java(
        """
        package androidx.fragment.app;

        public abstract class FragmentTransaction {
            public abstract FragmentTransaction replace(int containerViewId, Fragment fragment);
            public abstract int commit();
            public abstract int commitAllowingStateLoss();
            public abstract void commitNow();
        }
        """
    ).indented(),
)
