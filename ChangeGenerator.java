import java.util.HashMap;

public class ChangeGenerator {
	private int minCoinIn = 0;
	private int maxCoinIn = 10000;
	private int[] coinList = {200,100,50,20,10};
	private String[] coinListString = {"£2","£1","50p","20p","10p"};
	
	public int getMinCoinIn() {
		return minCoinIn;
	}
	
	public int getMaxCoinIn() {
		return maxCoinIn;
	}
	
	public String[] getCoinListString() {
		return coinListString;
	}

	public Boolean amountIsValid(int amount) {
		if (amount < minCoinIn || amount > maxCoinIn) {
			return false;
		}
		return true;
	}
	
	public Boolean excludedCoinIsValid(int coin) {
		for (int i=0; i < coinList.length; i++) {
			if (coin == coinList[i]) {
				return true;
			}
		}
		return false;
	}
	
	public int[] generateSingleCoinChange(int amount, int coinType) {
		int total = amount / coinType;
		int remainder = amount - (total * coinType);
		return new int[] {total, remainder};
	}
	
	public HashMap<Integer,Integer> generateMultiCoinChange(int amount, int excludedCoin) {
		int amountToProcess = amount;
		HashMap<Integer,Integer> result = new HashMap<>();
		
		for (int i=0; i < coinList.length; i++) {	
			if (coinList[i] == excludedCoin) {
				continue;
			}
			if (amountToProcess >= coinList[i]) {
				int numberOfCoins = amountToProcess / coinList[i];
				result.put(coinList[i], numberOfCoins);
				amountToProcess = amountToProcess - (numberOfCoins * coinList[i]);
			}	
		}
		if (amountToProcess > 0) {
			result.put(0, amountToProcess);
		}
		return result;
	}
}