package com.wiz.common.models;

import lombok.Getter;
import lombok.Setter;

public class BaseModel {
	private @Getter @Setter String regId;
	private @Getter @Setter String regDate;
	private @Getter @Setter String updId;
	private @Getter @Setter String updDate;
}