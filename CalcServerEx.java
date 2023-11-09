import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalcServerEx {
	public static String calc(String exp) {
		StringTokenizer st = new StringTokenizer(exp, " ");
		if (st.countTokens() > 3)
			return "error: too many argument"; // 인자가 세개 초과인 경우 많다고 오류 출력
		else if(st.countTokens() < 3)
			return "error: not enough argument"; // 인자가 세개 미만인 경우 적다고 오류 출력
		String res = "";
		int op1 = Integer.parseInt(st.nextToken());
		String opcode = st.nextToken();
		int op2 = Integer.parseInt(st.nextToken());
		switch (opcode) {
		case "+":
			res = Integer.toString(op1 + op2);
			break;
		case "-":
			res = Integer.toString(op1 - op2);
			break;
		case "*":
			res = Integer.toString(op1 * op2);
			break;
		case "/":
			// 나누는 수가 0일 경우 오류 출력
			if(op2 == 0) {
				res = "error: 0으로 나눌 수 없습니다.";
				break;
			}
			res = Double.toString((double)op1 / (double)op2);
			break;
		default:
			res = "error"; // 기타 오류일 경우의 오류
		}
		return res;
	}
	public static void main(String[] args) throws Exception {
		ServerSocket listener = new ServerSocket(9999);
		System.out.println("연결을 기다리고 있습니다.....");
		ExecutorService pool = Executors.newFixedThreadPool (20);
		while (true) {
			Socket sock = listener.accept();
			pool.execute(new Capitalizer(sock));
		}
	}
	private static class Capitalizer implements Runnable {
		private Socket socket;
		Capitalizer(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			System.out.println("연결되었습니다: " + socket);
			BufferedReader in = null;
			BufferedWriter out = null;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				while (true) {
					String inputMessage = in.readLine();
					if (inputMessage.equalsIgnoreCase("bye")) {
						System.out.println("클라이언트에서 연결을 종료하였음: " + socket);
						break; // "bye"를 받으면 연결 종료
					}
					System.out.println(inputMessage); // 받은 메시지를 화면에출력
					String res = calc(inputMessage); // 계산. 계산 결과는res
					out.write(res + "\n"); // 계산 결과 문자열 전송
					out.flush();
				}
			} catch (Exception e) {
				System.out.println("Error:" + socket);
			} finally {
				try { socket.close(); } catch (IOException e) {}
			}
		}
	}
}
