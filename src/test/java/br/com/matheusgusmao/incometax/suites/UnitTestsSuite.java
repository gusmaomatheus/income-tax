package br.com.matheusgusmao.incometax.suites;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Unit Tests Suite")
@SelectPackages("br.com.matheusgusmao.incometax.service")
public class UnitTestsSuite {
}
