package my.com.myriadeas.jpa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class TemplateGenerator {

	public static void generateVelocity(String szTemplate,
			VelocityContext context, String output) {
		Template template = null;
		System.out.println("Generating " + szTemplate);
		try {
			template = Velocity.getTemplate(szTemplate);
		} catch (ResourceNotFoundException rnfe) {
			// couldn't find the template
			rnfe.printStackTrace();
		} catch (ParseErrorException pee) {
			// syntax error: problem parsing the template
			pee.printStackTrace();
		} catch (MethodInvocationException mie) {
			// something invoked in the template
			// threw an exception
			mie.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		File file = new File(output);
		try {
			FileUtils.touch(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			FileWriter fw = new FileWriter(file);
			template.merge(context, fw);
			fw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Generated " + output);
	}
}
