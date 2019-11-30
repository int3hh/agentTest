/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agent.agentexample;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Executable;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.Listener;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.isSynthetic;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import net.bytebuddy.matcher.StringMatcher;
import net.bytebuddy.utility.JavaModule;


/**
 *
 * @author int3h
 */
public class Agent {


    public static void premain(String agentArgs, Instrumentation inst) {
        instrument(inst);
    }
    
    private static void instrument(Instrumentation inst) {
        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform(new LogMethodsInvocations())
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .installOn(inst);
    }
    
    
    public static void agentmain(String agentArgs, Instrumentation inst) throws IOException {
        System.out.println("Injecting! now");
          new AgentBuilder.Default()
                .with(RedefinitionStrategy.RETRANSFORMATION)
                .with(InitializationStrategy.NoOp.INSTANCE)
                .with(TypeStrategy.Default.REDEFINE)
                .ignore(new AgentBuilder.RawMatcher.ForElementMatchers(nameStartsWith("net.bytebuddy.").or(isSynthetic()), any(), any()))
                .with(new Listener.Filtering(
                        new StringMatcher("com.tester.jartest.App", StringMatcher.Mode.EQUALS_FULLY),
                        Listener.StreamWriting.toSystemOut()))
                .type(named("com.tester.jartest.App"))
                .transform((builder, type, classLoader, module) ->
                        builder.method(named("withdraw")).intercept(FixedValue.value(true))
                ).installOn(inst);
       
        System.out.println("Injected!");
    }
    
     public static void log(String msg) {
        System.out.println(msg);
    }

    private static class LogMethodsInvocations implements Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription,
                                                ClassLoader classLoader, JavaModule module) {
            log("load: " + typeDescription.getCanonicalName());

            final AsmVisitorWrapper methodsVisitor =
                    Advice.to(EnterMethodAdvice.class, ExitMethodAdvice.class)
                            .on(ElementMatchers.isMethod());

            final AsmVisitorWrapper constructorsVisitor =
                    Advice.to(EnterMethodAdvice.class, ExitMethodAdvice.class)
                            .on(ElementMatchers.isConstructor());

            return builder.visit(methodsVisitor).visit(constructorsVisitor);
        }

    }
    
        private static class EnterMethodAdvice {
        @Advice.OnMethodEnter
        static void onEnter(@Advice.Origin final Executable executable) {
            log("enter: " + executable.getName());
        }
    }

    private static class ExitMethodAdvice {
        @Advice.OnMethodExit
        static void onExit(@Advice.Origin final Executable executable) {
            log("exit: " + executable.getName());
        }
    }

}
