package org.recap.matchingalgorithm.service;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.executors.BibItemIndexExecutorService;
import org.recap.matchingalgorithm.MatchScoreUtil;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.MatchingAlgorithmReportDataEntity;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.MatchingAlgorithmReportDataDetailsRepository;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.service.ActiveMqQueuesInfo;
import org.recap.util.CommonUtil;
import org.recap.util.MatchingAlgorithmUtil;
import org.recap.util.SolrQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by premkb on 3/8/16.
 */

public class MatchingAlgorithmHelperServiceUT extends BaseTestCaseUT {



    @InjectMocks
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Mock
    MatchingAlgorithmHelperService matchingAlgoHelperService;

    @Mock
    private MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Mock
    private MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Mock
    private MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Mock
    private SolrQueryBuilder solrQueryBuilder;

    @Mock
    private SolrTemplate solrTemplate;

    @Mock
    private ActiveMqQueuesInfo activeMqQueuesInfo;

    @Mock
    private ProducerTemplate producerTemplate;


    @Mock
    CommonUtil commonUtil;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    MatchingAlgorithmReportDataDetailsRepository matchingAlgorithmReportDataDetailsRepository;

    @Mock
    MatchingAlgorithmReportDataEntity matchingAlgorithmReportDataEntity;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    EntityManager entityManager;

    @Mock
    BibItemIndexExecutorService bibItemIndexExecutorService;

    @Mock
    Set<Integer> bibIdsToIndex;


    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(matchingAlgorithmHelperService,"isIndexGrouping",true);
    }

    @Test
    public void groupBibsForMonographPendingMatch() throws Exception {
        List<MatchingAlgorithmReportDataEntity> reportDataEntities=new ArrayList<>();
        reportDataEntities.add(matchingAlgorithmReportDataEntity);
        Mockito.when(matchingAlgorithmReportDataDetailsRepository.getCountOfRecordNumForMatchingPendingMonograph(ScsbCommonConstants.BIB_ID)).thenReturn(1l);
        Map<Integer, BibliographicEntity> bibliographicEntityMap=new HashMap<>();
        bibliographicEntityMap.put(1,bibliographicEntity);
        ReflectionTestUtils.setField(matchingAlgorithmUtil,"bibliographicDetailsRepository",bibliographicDetailsRepository);
        ReflectionTestUtils.setField(matchingAlgorithmUtil,"entityManager",entityManager);
        matchingAlgorithmHelperService.groupBibsForMonograph(1,true);
    }

    @Test
    public void groupBibsForMonograph() throws Exception {
         matchingAlgorithmHelperService.groupBibsForMonograph(1,false);
    }

    @Test
    public void removeMatchingIdsInSolr() throws Exception {
        Mockito.when(bibIdsToIndex.isEmpty()).thenReturn(false).thenReturn(true);
        Mockito.when(bibItemIndexExecutorService.partialIndex(any())).thenReturn(1);
        Mockito.when(matchingAlgorithmUtil.getBibIdsToRemoveMatchingIdsInSolr()).thenReturn(bibIdsToIndex);
        matchingAlgorithmHelperService.removeMatchingIdsInSolr();
    }

    @Test
    public void removeMatchingIdsInDB() throws Exception {
        matchingAlgorithmHelperService.removeMatchingIdsInDB();
    }

    @Test
    public void groupBibsForMVMS() throws Exception {
        Map<Integer, BibliographicEntity> bibliographicEntityMap=new HashMap<>();
        bibliographicEntityMap.put(1,bibliographicEntity);
        List<MatchingAlgorithmReportDataEntity> reportDataEntities=new ArrayList<>();
        reportDataEntities.add(matchingAlgorithmReportDataEntity);
        Mockito.when(matchingAlgorithmReportDataEntity.getRecordNum()).thenReturn("1");
        Mockito.when(matchingAlgorithmReportDataEntity.getHeaderName()).thenReturn(ScsbConstants.BIB_ID);
        Mockito.when(matchingAlgorithmReportDataEntity.getHeaderValue()).thenReturn("1");
        Mockito.when(matchingAlgorithmReportDataDetailsRepository.getCountOfRecordNumForMatchingMVMs(ScsbCommonConstants.BIB_ID)).thenReturn(1l);
        Mockito.when(matchingAlgorithmReportDataDetailsRepository.getReportDataEntityForMatchingMVMs(Arrays.asList(ScsbCommonConstants.BIB_ID, ScsbConstants.MATCH_SCORE),0,1)).thenReturn(reportDataEntities);
        ReflectionTestUtils.setField(matchingAlgorithmHelperService,"isIndexGrouping",Boolean.TRUE);
        matchingAlgorithmHelperService.groupBibsForMVMs(1);
    }

    @Test
    public void groupBibsForSerials() throws Exception {
        Map<Integer, BibliographicEntity> bibliographicEntityMap=new HashMap<>();
        bibliographicEntityMap.put(1,bibliographicEntity);
        List<MatchingAlgorithmReportDataEntity> reportDataEntities=new ArrayList<>();
        reportDataEntities.add(matchingAlgorithmReportDataEntity);
        Mockito.when(matchingAlgorithmReportDataEntity.getRecordNum()).thenReturn("1");
        Mockito.when(matchingAlgorithmReportDataEntity.getHeaderName()).thenReturn(ScsbConstants.BIB_ID);
        Mockito.when(matchingAlgorithmReportDataEntity.getHeaderValue()).thenReturn("1");
        Mockito.when(matchingAlgorithmReportDataDetailsRepository.getCountOfRecordNumForMatchingSerials(ScsbCommonConstants.BIB_ID)).thenReturn(1l);
        Mockito.when(matchingAlgorithmReportDataDetailsRepository.getReportDataEntityForMatchingSerials(Arrays.asList(ScsbCommonConstants.BIB_ID, ScsbConstants.MATCH_SCORE),0,1)).thenReturn(reportDataEntities);
        ReflectionTestUtils.setField(matchingAlgorithmHelperService,"isIndexGrouping",Boolean.TRUE);
        matchingAlgorithmHelperService.groupForSerialBibs(1);
    }

    private MatchingMatchPointsEntity getMatchingMatchPointEntity() {
        MatchingMatchPointsEntity matchingMatchPointsEntity = new MatchingMatchPointsEntity();
        matchingMatchPointsEntity.setMatchCriteria(ScsbCommonConstants.OCLC_CRITERIA);
        matchingMatchPointsEntity.setCriteriaValue("193843");
        matchingMatchPointsEntity.setCriteriaValueCount(4);
        matchingMatchPointsEntity.setId(1);
        return matchingMatchPointsEntity;
    }

    private MatchingBibEntity getMatchingBibEntity(String matching) {
        MatchingBibEntity matchingBibEntity = new MatchingBibEntity();
        matchingBibEntity.setId(1);
        matchingBibEntity.setRoot("123");
        matchingBibEntity.setStatus("Pending");
        matchingBibEntity.setLccn("19383");
        matchingBibEntity.setBibId(1);
        matchingBibEntity.setIsbn("883939");
        matchingBibEntity.setOwningInstitution("PUL");
        matchingBibEntity.setMatching(matching);
        matchingBibEntity.setMaterialType("Monograph");
        matchingBibEntity.setOclc("2939384");
        matchingBibEntity.setIssn("29384");
        matchingBibEntity.setOwningInstBibId("1938");
        matchingBibEntity.setTitle("Sample Matching Title");
        return matchingBibEntity;
    }

    @Test
    public void findMatchingAndPopulateMatchPointsEntities() throws Exception {
        List<MatchingMatchPointsEntity> matchingMatchPointsEntities = new ArrayList<>();
        matchingMatchPointsEntities.add(getMatchingMatchPointEntity());
        Mockito.when(matchingAlgorithmUtil.getMatchingMatchPointsEntity(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC)).thenReturn(matchingMatchPointsEntities);
        Mockito.when(matchingAlgorithmUtil.getMatchingMatchPointsEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN)).thenReturn(matchingMatchPointsEntities);
        Mockito.when(matchingAlgorithmUtil.getMatchingMatchPointsEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISSN)).thenReturn(matchingMatchPointsEntities);
        Mockito.when(matchingAlgorithmUtil.getMatchingMatchPointsEntity(ScsbCommonConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(matchingMatchPointsEntities);
        Mockito.doNothing().when(matchingAlgorithmUtil).saveMatchingMatchPointEntities(matchingMatchPointsEntities);
        long count = matchingAlgorithmHelperService.findMatchingAndPopulateMatchPointsEntities();
        assertNotNull(count);
        assertEquals(count, matchingMatchPointsEntities.size() * 4);
    }

    @Test
    public void populateMatchingBibEntities() throws Exception {
        Mockito.when(matchingMatchPointsDetailsRepository.countBasedOnCriteria(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC)).thenReturn(Long.valueOf(0));
        Mockito.when(matchingMatchPointsDetailsRepository.countBasedOnCriteria(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN)).thenReturn(Long.valueOf(0));
        Mockito.when(matchingMatchPointsDetailsRepository.countBasedOnCriteria(ScsbCommonConstants.MATCH_POINT_FIELD_ISSN)).thenReturn(Long.valueOf(0));
        Mockito.when(matchingMatchPointsDetailsRepository.countBasedOnCriteria(ScsbCommonConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(Long.valueOf(0));
        long count = matchingAlgorithmHelperService.populateMatchingBibEntities();
        assertNotNull(count);
        assertEquals(count, 0);
    }

    @Test
    public void populateReportsForOCLCAndISBN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(oclcAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getOclc());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndIsbn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);
        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN,getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    private Map<String, Integer> getStringIntegerMap() {
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put(ScsbConstants.PUL_MATCHING_COUNT, 1);
        countMap.put(ScsbConstants.CUL_MATCHING_COUNT, 2);
        countMap.put(ScsbConstants.NYPL_MATCHING_COUNT, 3);
        return countMap;
    }

    @Test
    public void populateReportsForOCLCAndISSN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(oclcAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getOclc());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndIssn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, ScsbCommonConstants.MATCH_POINT_FIELD_ISSN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);

        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, ScsbCommonConstants.MATCH_POINT_FIELD_ISSN, getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForOCLCAndLCCN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(oclcAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getOclc());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndLccn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, ScsbCommonConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);
        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, ScsbCommonConstants.MATCH_POINT_FIELD_LCCN, getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForISBNAndISSN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(isbnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIsbn());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndIssn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, ScsbCommonConstants.MATCH_POINT_FIELD_ISSN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(isbnAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);

        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, ScsbCommonConstants.MATCH_POINT_FIELD_ISSN, getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForISBNAndLCCN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(isbnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIsbn());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndLccn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, ScsbCommonConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(isbnAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);

        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, ScsbCommonConstants.MATCH_POINT_FIELD_LCCN, getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForOCLCAndSolr() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, matchingBibEntity)).thenCallRealMethod();
//        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(issnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIssn());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndTitle()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, ScsbConstants.TITLE_MATCH_SOLR)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(issnAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
//        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(Mockito.any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);

        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000,  ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, ScsbConstants.TITLE_MATCH_SOLR, getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForOCLCAndtitle() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC);
        matchingBibEntity.setBibId(1);
        matchingBibEntity.setBibId(2);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        bibIdSet.add(1);
        bibIdSet.add(2);
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, matchingBibEntity)).thenCallRealMethod();
     //  Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(issnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIssn());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndTitle()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, ScsbCommonConstants.MATCH_POINT_FIELD_TITLE)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(issnAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
//       Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(Mockito.any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);

        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000,  ScsbCommonConstants.MATCH_POINT_FIELD_OCLC,ScsbCommonConstants.MATCH_POINT_FIELD_TITLE, getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForLCCNAndSolr() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_LCCN);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
//        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_LCCN, matchingBibEntity)).thenCallRealMethod();
//        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(issnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIssn());
//        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForIssnAndTitle()).thenReturn(bibIds);
//        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_LCCN, ScsbConstants.TITLE_MATCH_SOLR)).thenReturn(matchingBibEntities);
//        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(issnAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_LCCN, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
//        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(Mockito.any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);

        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000,  ScsbCommonConstants.MATCH_POINT_FIELD_LCCN, ScsbConstants.TITLE_MATCH_SOLR, getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }


    @Test

    public void populateReportsForISSNAndSolr() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISSN);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_ISSN, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(issnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIssn());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForIssnAndTitle()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_ISSN, ScsbConstants.TITLE_MATCH_SOLR)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(issnAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_ISSN, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);
        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000,  ScsbCommonConstants.MATCH_POINT_FIELD_ISSN, ScsbConstants.TITLE_MATCH_SOLR, getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForISBNAndSolr() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, matchingBibEntity)).thenCallRealMethod();
//        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(issnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIssn());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndTitle()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, ScsbConstants.TITLE_MATCH_SOLR)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(issnAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
//        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(Mockito.any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);

        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000,  ScsbCommonConstants.MATCH_POINT_FIELD_ISBN, ScsbConstants.TITLE_MATCH_SOLR, getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForISSNAndLCCN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_ISSN);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String, Integer> countMap = getStringIntegerMap();
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(ScsbCommonConstants.MATCH_POINT_FIELD_ISSN, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(issnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIssn());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForIssnAndLccn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, ScsbCommonConstants.MATCH_POINT_FIELD_ISSN, ScsbCommonConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(issnAndBibIdMap, matchingBibEntities, ScsbCommonConstants.MATCH_POINT_FIELD_ISSN, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(any(),Mockito.anyMap(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyMap(),Mockito.anyInt())).thenReturn(countMap);

        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForMatchPoints(1000,  ScsbCommonConstants.MATCH_POINT_FIELD_ISSN, ScsbCommonConstants.MATCH_POINT_FIELD_LCCN, getStringIntegerMap(), MatchScoreUtil.OCLC_ISBN_SCORE);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForSingleMatch() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(ScsbCommonConstants.MATCH_POINT_FIELD_OCLC);
        matchingBibEntities.add(matchingBibEntity);
        Map<String, Integer> countMap = getStringIntegerMap();
        Set<Integer> matchingBibIds = new HashSet<>();
        List<MatchingBibEntity> matchingBibEntityList = getMatchingBibEntity(matchingBibEntities).getContent();
        List<String> allInstitutionCodeExceptSupportInstitution=Arrays.asList(ScsbCommonConstants.COLUMBIA,ScsbCommonConstants.PRINCETON,ScsbCommonConstants.NYPL);
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(allInstitutionCodeExceptSupportInstitution);
        matchingAlgorithmHelperService.saveMatchingSummaryCount(getStringIntegerMap());
        Mockito.when(matchingBibDetailsRepository.findByStatus(PageRequest.of(0,1000), ScsbConstants.PENDING)).thenReturn(getMatchingBibEntity(matchingBibEntities));
        Mockito.when(matchingBibDetailsRepository.findByStatus(PageRequest.of(1,1000), ScsbConstants.PENDING)).thenReturn(getMatchingBibEntity(matchingBibEntities));
        Mockito.when(matchingAlgorithmUtil.processPendingMatchingBibs(matchingBibEntityList,matchingBibIds, getStringIntegerMap())).thenReturn(countMap);
        Map<String, Integer> countsMap = matchingAlgorithmHelperService.populateReportsForSingleMatch(1000, getStringIntegerMap());
        assertNotNull(ScsbConstants.PUL_MATCHING_COUNT);
        assertNotNull(ScsbConstants.CUL_MATCHING_COUNT);
        assertNotNull(ScsbConstants.NYPL_MATCHING_COUNT);

    }

    @Test
    public void checkGetterServices() throws Exception {
        Mockito.when(matchingAlgoHelperService.getActiveMqQueuesInfo()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getMatchingBibDetailsRepository()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getMatchingAlgorithmUtil()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getMatchingMatchPointsDetailsRepository()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getProducerTemplate()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getSolrQueryBuilder()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getSolrTemplate()).thenCallRealMethod();
        assertNotEquals(activeMqQueuesInfo, matchingAlgoHelperService.getActiveMqQueuesInfo());
        assertNotEquals(matchingBibDetailsRepository, matchingAlgoHelperService.getMatchingBibDetailsRepository());
        assertNotEquals(matchingAlgorithmUtil, matchingAlgoHelperService.getMatchingAlgorithmUtil());
        assertNotEquals(solrQueryBuilder, matchingAlgoHelperService.getSolrQueryBuilder());
        assertNotEquals(solrTemplate, matchingAlgoHelperService.getSolrTemplate());
        assertNotEquals(producerTemplate, matchingAlgoHelperService.getProducerTemplate());
        assertNotEquals(matchingMatchPointsDetailsRepository, matchingAlgoHelperService.getMatchingMatchPointsDetailsRepository());
    }

    @Test
    public void runReportsForMatchingAlgorithm() {
        List<String> strings = new ArrayList<>();
        strings.add("PUL");
        Integer batchSize = 2;
        Map<String, Integer> institutionCounterMap = new HashMap<>();
        institutionCounterMap.put("test", 1);
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(strings);
        matchingAlgorithmHelperService.runReportsForMatchingAlgorithm(batchSize);
    }

    @Test
    public void populateReportsForMultiMatch() {
        Integer batchSize = 10;
        Map<String, Integer> institutionCounterMap = new HashMap<>();
        institutionCounterMap.put("PUL", 1);
        Mockito.when(matchingAlgorithmUtil.getMatchPointsCombinationMap()).thenReturn(institutionCounterMap);
        matchingAlgorithmHelperService.populateReportsForMultiMatch(batchSize, institutionCounterMap);
    }


    @Test
    public void collectFuturesAndProcessForReports() {
        Map<String, Integer> institutionCounterMap = new HashMap<>();
        institutionCounterMap.put("test", 1);
        List<Future> futures = new ArrayList<>();
        Future future = CompletableFuture.completedFuture(institutionCounterMap);
        futures.add(future);

        ReflectionTestUtils.invokeMethod(matchingAlgorithmHelperService, "collectFuturesAndProcessForReports", futures, institutionCounterMap);
    }

    @Test
    public void collectFuturesAndProcessForReports_else() {
        Map<String, Integer> institutionCounterMap = new HashMap<>();
        institutionCounterMap.put("PUL", 1);
        List<Future> futures = new ArrayList<>();
        Future future = CompletableFuture.completedFuture(institutionCounterMap);
        futures.add(future);

        ReflectionTestUtils.invokeMethod(matchingAlgorithmHelperService, "collectFuturesAndProcessForReports", futures, institutionCounterMap);
    }

    public Page<MatchingBibEntity> getMatchingBibEntity(List<MatchingBibEntity> matchingBibEntities){
        Page<MatchingBibEntity> matchingBibEntityPage = new Page<MatchingBibEntity>() {
            @Override
            public int getTotalPages() {
                return 2;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super MatchingBibEntity, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<MatchingBibEntity> getContent() {
                return null;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<MatchingBibEntity> iterator() {
                return matchingBibEntities.iterator();
            }
        };

        return matchingBibEntityPage;
    }
}
