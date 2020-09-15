public Response updateSound(MultipartFormDataInput input, @PathParam("severity") String severityStr) {
	if (severityStr == null || severityStr.length() == 0) {
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
	try {
		AlarmSeverityVO severity = AlarmSeverityVO.valueOf(severityStr.toUpperCase());

		InputStream stream = input.getFormDataPart(UPLOADED_FILE_PARAMETER_NAME, InputStream.class, null);
		byte[] fileDate = IOUtils.toByteArray(stream);
		boolean checkFileType = CheckFileTypeUtil.checkFileType(fileDate, "wav");
		if (!checkFileType) {
			throw new SscAlarmException(SscAlarmException.SoundFileNotAWavFileError);
		}
		this.alarmSettingsService.updateSound(severity, fileDate);
	} catch (IOException e) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
	}
	return Response.status(Response.Status.OK).build();
}
