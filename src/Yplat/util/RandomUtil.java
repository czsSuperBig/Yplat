package Yplat.util;

import java.util.Random;
import java.util.UUID;

public class RandomUtil {

	private static int[] primes = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37,
			41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107,
			109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179,
			181, 191, 193, 197, 199 };
	
	private static char[] charSequences = "0QwErTyUiOpAsDfGhJkLzXcVbNm12345678qWeRtYuIoPaSdFgHjKlZxCvBnM9_".toCharArray();

	/**
	 * 获取步长
	 * 
	 * @return
	 */
	public static int getPrimes() {
		return primes[(int) (Math.random() * (primes.length))];
	}

	/**
	 * 获取基准�?
	 * 
	 * @return
	 */
	public static int getBaseNum() {
		return (int) Math.round(Math.random() * 8999 + 1000);
	}
	
	/**
	 * 
	 * 随机生成length位随机数，包含数字和字母
	 */
	public static String getRandomSequence(int length)
	{
//		char[] charSequences = { '1', '2', '3', '4', '5', '6', '7', '8', '9','0','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		StringBuffer randomCode = new StringBuffer();
		
		Random random = new Random();
//		random.setSeed(System.currentTimeMillis()+12615);
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(charSequences[random.nextInt(charSequences.length)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}
	
	/**
	 * 获取强随机数.
	 * @comment 
	 * @param length
	 * @return
	 */
	public static String getStrongRandomSequence(int length)
	{
		StringBuffer strBuffer = new StringBuffer(length);
		
		int pieceLen = 4;
		for ( int i = 0; i < length; i += 4)
		{
			pieceLen = 4;
			if ( (length-i) < 4 )
			{
				pieceLen = (length-i);
			}
			
			strBuffer.append(getRandomSequence(pieceLen));
		}
		
		return strBuffer.toString();
	}
	
	/**
	 * 
	 * 随机生成length位数纯数字新密码
	 */
	public static String randomInt(int length) {
		char[] charSequences = { '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'0' };
		StringBuffer randomCode = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(charSequences[random.nextInt(10)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}

	/**
	 * 获取UUID编号.
	 * @comment 
	 * @return
	 */
	public static String getUUID()
	{
		UUID uuid = UUID.randomUUID();
		return getRandomSequence(4)+(uuid.toString().replace("-", ""));
	}
	
}
