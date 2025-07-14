package com.nksoft.entrance_examination.common.config.graalvm;

import com.nksoft.entrance_examination.common.config.graalvm.hints.HibernateHints;
import com.nksoft.entrance_examination.common.config.graalvm.hints.LiquibaseHints;
import com.nksoft.entrance_examination.common.advice.ErrorResponse;
import com.nksoft.entrance_examination.common.config.graalvm.hints.NormalizationHints;
import com.nksoft.entrance_examination.common.config.graalvm.hints.PlacementHints;
import com.nksoft.entrance_examination.common.config.graalvm.hints.StudentChoiceHints;
import com.nksoft.entrance_examination.common.validator.classes.DecimalPrecisionValidator;
import com.nksoft.entrance_examination.common.validator.classes.EnumValidator;
import com.nksoft.entrance_examination.department.dto.DepartmentDto;
import com.nksoft.entrance_examination.examination.dto.ExamCenterDto;
import com.nksoft.entrance_examination.examination.dto.ExamDto;
import com.nksoft.entrance_examination.examination.dto.ExamEntryDto;
import com.nksoft.entrance_examination.student.dto.LoginDto;
import com.nksoft.entrance_examination.student.dto.StudentDto;
import com.nksoft.entrance_examination.department.dto.UniversityDto;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.domain.PageImpl;

@Configuration
@RegisterReflectionForBinding({
        UniversityDto.class, DepartmentDto.class,
        ExamCenterDto.class, ExamDto.class,
        ExamEntryDto.class, StudentDto.class, LoginDto.class,
        ErrorResponse.class,
        EnumValidator.class, DecimalPrecisionValidator.class,
        PageImpl.class })
@ImportRuntimeHints({
        LiquibaseHints.class,
        HibernateHints.class,
        StudentChoiceHints.class,
        NormalizationHints.class,
        PlacementHints.class })
public class NativeImageConfig {}