/**
 *    Copyright 2015-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.caches.redis;

import java.io.Serializable;
import java.util.ArrayList;

public class SimpleBeanStudentInfo implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  String name;
  int age;
  int grade;
  String sex;
  ArrayList<String> courses;

  public SimpleBeanStudentInfo() {
    // TODO Auto-generated constructor stub
    this.name = "Kobe Bryant";
    this.age = 40;
    this.grade = 12;
    this.sex = "MALE";
    this.courses = new ArrayList<String>();
    this.courses.add("English");
    this.courses.add("Math");
    this.courses.add("Physics");
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public int getGrade() {
    return grade;
  }

  public void setGrade(int grade) {
    this.grade = grade;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  @Override
  public String toString() {
    return "StudentInfo [name=" + name + ", age=" + age + ", grade=" + grade + ", sex=" + sex + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + age;
    result = prime * result + ((courses == null) ? 0 : courses.hashCode());
    result = prime * result + grade;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((sex == null) ? 0 : sex.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SimpleBeanStudentInfo other = (SimpleBeanStudentInfo) obj;
    if (age != other.age) return false;
    if (courses == null) {
      if (other.courses != null) return false;
    } else if (!courses.equals(other.courses)) return false;
    if (grade != other.grade) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (sex == null) {
      if (other.sex != null) return false;
    } else if (!sex.equals(other.sex)) return false;
    return true;
  }

}
