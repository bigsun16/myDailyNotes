/*if (severityStr == null || severityStr.length() == 0) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		AlarmSeverity severity = AlarmSeverity.valueOf(severityStr.toUpperCase());
		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("file");

		for (InputPart inputPart : inputParts) {
			
			
			MultivaluedMap<String, String> headers = inputPart.getHeaders();
			try {
				InputStream inputStream = inputPart.getBody(InputStream.class, null);
				byte[] fileDate = IOUtils.toByteArray(inputStream);
				String filename = getFileName(headers);
				if (!filename.endsWith(".wav")) {
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
				this.alarmSettingsService.updateSound(severity, fileDate);
			} catch (IOException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			}
		}
		return Response.status(Response.Status.OK).build();*/
		
		
		/*   private String getFileName(MultivaluedMap<String, String> headers) {
	        String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");

	        for (String filename : contentDisposition) {
	            if ((filename.trim().startsWith("filename"))) {

	                String[] name = filename.split("=");

	                String finalFileName = name[1].trim().replace("\"", "");
					return finalFileName;
					}
	        }
	        return "unknown";
	    }
*/