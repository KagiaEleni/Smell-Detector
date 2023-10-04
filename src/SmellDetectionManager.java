
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SmellDetectionManager {
	
	private SmellType smellTypeToBeDetected;
	private List<SmellDetector> smellDetectors;
	private String projectDirectory;
	private Map<SmellType, Set<Smell>> detectedSmells;
	private Map<SmellType, Integer> mapFromSmellNameToMaxToolCount;

	
	public SmellDetectionManager(SmellType smellType, String projectDirectory) {
		this.smellTypeToBeDetected = smellType;
        this.projectDirectory = projectDirectory;
        try {
			initialiseNecessaryClassFields();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initialiseNecessaryClassFields() {

		detectedSmells = new HashMap<>();
        mapFromSmellNameToMaxToolCount = new HashMap<>();
        
        smellDetectors = new ArrayList<>(6);
        boolean useAllDetectors = (smellTypeToBeDetected == SmellType.ALL_SMELLS);
        /*
        if (useAllDetectors || smellTypeToBeDetected == SmellType.PMD) {
        	 PMDSmellDetector pmdDetector = new PMDSmellDetector(projectDirectory);
        	 try {
				pmdDetector.findSmells(smellTypeToBeDetected, detectedSmells);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             smellDetectors.add(pmdDetector);
             updateMaxToolCountMap(pmdDetector);
        }
        
        if (useAllDetectors || smellTypeToBeDetected == SmellType.CHECKSTYLE) {
            CheckStyleSmellDetector checkStyleDetector = new CheckStyleSmellDetector(projectDirectory);
            checkStyleDetector.findSmells(smellTypeToBeDetected, detectedSmells);
            smellDetectors.add(checkStyleDetector);
            updateMaxToolCountMap(checkStyleDetector);
        }
        
        if (useAllDetectors || smellTypeToBeDetected == SmellType.DUDE) {
            DuDeSmellDetector dudeDetector = new DuDeSmellDetector(projectDirectory);
            try {
				dudeDetector.findSmells(smellTypeToBeDetected, detectedSmells);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            smellDetectors.add(dudeDetector);
            updateMaxToolCountMap(dudeDetector);
        }
        
        if (useAllDetectors || smellTypeToBeDetected == SmellType.JSPIRIT) {
            JSpIRITSmellDetector jspiritDetector = new JSpIRITSmellDetector(projectDirectory);
            try {
				jspiritDetector.findSmells(smellTypeToBeDetected, detectedSmells);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            smellDetectors.add(jspiritDetector);
            updateMaxToolCountMap(jspiritDetector);
        }*/
        
        if (useAllDetectors || smellTypeToBeDetected == SmellType.JDEODORANT) {
            JDeodorantSmellDetector jdeodorantDetector = new JDeodorantSmellDetector(projectDirectory);
            try {
				jdeodorantDetector.findSmells(smellTypeToBeDetected, detectedSmells);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            smellDetectors.add(jdeodorantDetector);
            updateMaxToolCountMap(jdeodorantDetector);
        }/*
        
        if (useAllDetectors || smellTypeToBeDetected == SmellType.ORGANIC) {
            OrganicSmellDetector organicDetector = new OrganicSmellDetector(projectDirectory);
            try {
				organicDetector.findSmells(smellTypeToBeDetected, detectedSmells);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            smellDetectors.add(organicDetector);
            updateMaxToolCountMap(organicDetector);
        }*/
	}
	
	/**
	 * Parses the supported smell types for the given detector and updates the max count of
	 * the enabled tools which can detect each smell type.
	 * 
	 * @param detector the {@code SmellDetector} from which the smell types will be parsed
	 */
        
	private void updateMaxToolCountMap(SmellDetector detector) {
		detector.getSupportedSmellTypes().forEach( smellType -> {
			mapFromSmellNameToMaxToolCount.merge(smellType, 1, (oldValue, newValue) -> oldValue + newValue);
		});
	}

}

