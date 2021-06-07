package com.johan;

import com.johan.generic.TestRunner;

public final class App {

    private App() {
        TestRunner runner = new TestRunner("rabbit");
        runner.startTests();
        runner.endTests();
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        App app = new App();
    }
}
