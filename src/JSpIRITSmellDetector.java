import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import spirit.changes.manager.CodeChanges;
import spirit.core.design.AgglomerationManager;
import spirit.core.design.CodeSmellsManager;
import spirit.core.design.CodeSmellsManagerFactory;
import spirit.core.smells.CodeSmell;
import spirit.dependencies.DependencyVisitor;
import spirit.dependencies.Graph;
import spirit.metrics.storage.InvokingCache;

public class JSpIRITSmellDetector extends SmellDetector {
    
	private String projectDirectory;
	private IProject IProject;

	public JSpIRITSmellDetector(String projectDirectory) {
	    this.projectDirectory = projectDirectory;
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    this.IProject = root.getProject(projectDirectory);
	}

    private static final Set<SmellType> SUPPORTED_SMELL_TYPES = Collections.unmodifiableSet(
            new HashSet<SmellType>(Arrays.asList(SmellType.BRAIN_CLASS,
                                                SmellType.BRAIN_METHOD,
                                                SmellType.LONG_METHOD,
                                                SmellType.DATA_CLASS,
                                                SmellType.DISPERSED_COUPLING,
                                                SmellType.FEATURE_ENVY,
                                                SmellType.GOD_CLASS,
                                                SmellType.INTENSIVE_COUPLING,
                                                SmellType.REFUSED_PARENT_BEQUEST,
                                                SmellType.SHOTGUN_SURGERY,
                                                SmellType.TRADITION_BREAKER)));
    
    @Override
    public Set<SmellType> getSupportedSmellTypes() {
        return SUPPORTED_SMELL_TYPES;
    }

    @Override
    public String getDetectorName() {
        return "JSpIRIT";
    }

    /**
     * A map that contains the code smells detected from the tool as the key, and their
     * corresponding {@code SmellType} as the value.
     */
    private static final Map<String, SmellType> MAP_FROM_DECTECTED_SMELLS_TO_SMELLTYPE;
    static {
        Map<String, SmellType> tempMap = new HashMap<String, SmellType>(11);
        tempMap.put("Brain Class", SmellType.BRAIN_CLASS);
        tempMap.put("Brain Method", SmellType.BRAIN_METHOD);
        tempMap.put("Long Method", SmellType.LONG_METHOD);
        tempMap.put("Data Class", SmellType.DATA_CLASS);
        tempMap.put("Dispersed Coupling", SmellType.DISPERSED_COUPLING);
        tempMap.put("Feature Envy", SmellType.FEATURE_ENVY);
        tempMap.put("God Class", SmellType.GOD_CLASS);
        tempMap.put("Intensive Coupling", SmellType.INTENSIVE_COUPLING);
        tempMap.put("Refused Parent Bequest", SmellType.REFUSED_PARENT_BEQUEST);
        tempMap.put("Shotgun Surgery", SmellType.SHOTGUN_SURGERY);
        tempMap.put("Tradition Breaker", SmellType.TRADITION_BREAKER);
        MAP_FROM_DECTECTED_SMELLS_TO_SMELLTYPE = Collections.unmodifiableMap(tempMap);
    }
    
    @Override
    public void findSmells(SmellType smellType, Map<SmellType, Set<Smell>> detectedSmells) throws Exception {
    	CodeSmellsManagerFactory.getInstance().setCurrentProject(IProject);
		CodeSmellsManager codeSmellManager = CodeSmellsManagerFactory.getInstance().getCurrentProjectManager();
		codeSmellManager.initialize();
		InvokingCache.getInstance().initialize();
		AgglomerationManager agglomerationManager = AgglomerationManager.getInstance();
		agglomerationManager.setCurrentProject(projectDirectory);
		
        try {
            codeSmellManager.setVisitOnlyModified(false);
            Graph graph = DependencyVisitor.getInstance().getGraph(projectDirectory);
            if (!CodeChanges.getInstance().isNeedReScan() && graph.countVertexes() > 0) {
                CodeChanges changes = CodeChanges.getInstance();
                Set<String> modClasses = null;
                if (changes.anyChanges())
                    modClasses = graph.getTouchedClasses(changes);
                if (modClasses != null) {
                    codeSmellManager.setVisitOnlyModified(true);
                    codeSmellManager.setModifiedClasses(modClasses);
                }
            } else {
                codeSmellManager.setVisitDependencies(true);
            }

            codeSmellManager.calculateMetricsCode();
            codeSmellManager.calculateAditionalMetrics();
            codeSmellManager.detectCodeSmells();

            Vector<CodeSmell> codeSmells = codeSmellManager.getSmells();
            for (CodeSmell smell: codeSmells) {
                SmellType detectedSmellType = MAP_FROM_DECTECTED_SMELLS_TO_SMELLTYPE.get(smell.getKindOfSmellName());
                if(smellType != SmellType.ALL_SMELLS && smellType != detectedSmellType)
                    continue;
                
                String mainClassName = smell.getMainClass().toString();
                String[] packageNameSegments = mainClassName.split("\\.");
                String fileName = packageNameSegments[packageNameSegments.length - 1] + ".java";
                String packagePath = String.join("/", Arrays.copyOf(packageNameSegments, packageNameSegments.length - 1));
                String filePath = projectDirectory + "/src/" + packagePath + "/" + fileName;
                File targetIFile = new File(filePath);

                if(Utils.isClassSmell(detectedSmellType)) {
                    Utils.addSmell(detectedSmellType, detectedSmells, getDetectorName(),
                            Utils.createSmellObject(detectedSmellType, smell.getElementName(), targetIFile, smell.getLine()));
                } else {
                    String[] elementName = smell.getElementName().split("\\.");
                    Utils.addSmell(detectedSmellType, detectedSmells, getDetectorName(),
                            Utils.createSmellObject(detectedSmellType, elementName[0], elementName[1], targetIFile, smell.getLine()));
                }
            }
        } catch (Exception e) {
            // Handle exceptions appropriately for your project.
            e.printStackTrace();
        }
    }
    
}
