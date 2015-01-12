package com.minorityhobbies.util.ee;

import javax.batch.runtime.JobExecution;

public interface BatchStatusEnabled {
	BatchStatusMailTemplate onSuccess(JobExecution execution);
	BatchStatusMailTemplate onFailure(JobExecution execution);
}
