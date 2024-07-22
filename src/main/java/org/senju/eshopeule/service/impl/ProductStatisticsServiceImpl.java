package org.senju.eshopeule.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.senju.eshopeule.dto.OrderedProductStatDTO;
import org.senju.eshopeule.dto.ProdOrderStatusStatDTO;
import org.senju.eshopeule.dto.ProdRepeatPurchaseRateDTO;
import org.senju.eshopeule.dto.request.ProductOrderStatusStatRequest;
import org.senju.eshopeule.dto.response.OrderedProductStatPagingResponse;
import org.senju.eshopeule.dto.response.ProductRepeatPurchaseRatePagingResponse;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.repository.jpa.ProductRepository;
import org.senju.eshopeule.service.ProductStatisticsService;
import org.senju.eshopeule.utils.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.senju.eshopeule.constant.exceptionMessage.ProductExceptionMsg.PRODUCT_NOT_FOUND_WITH_ID_MSG;

@Service
@RequiredArgsConstructor
public class ProductStatisticsServiceImpl implements ProductStatisticsService {

    private final SqlSession sqlSession;
    private final ProductRepository productRepository;

    private static final String NAMESPACE = "org.senju.mybatis.ProductXmlMapper";
    private static final Logger logger = LoggerFactory.getLogger(ProductStatisticsService.class);

    @Override
    public List<ProdOrderStatusStatDTO> getProductOrderStatusStatistics(ProductOrderStatusStatRequest request) {
        if (!productRepository.existsById(request.getProductId())) {
            throw new NotFoundException(String.format(PRODUCT_NOT_FOUND_WITH_ID_MSG, request.getProductId()));
        }
        if (request.getEndDate() == null) request.setEndDate(LocalDateTime.now());
        if (request.getStartDate() == null) request.setStartDate(request.getEndDate().minusWeeks(1));
        Map<String, Object> sqlParameters = Map.of(
                "productId", request.getProductId(),
                "startDate", request.getStartDate(),
                "endDate", request.getEndDate()
        );
        return sqlSession.selectList(NAMESPACE + ".getProductOrderStatusStatistics", sqlParameters);
    }

    @Override
    public OrderedProductStatPagingResponse getOrderedProductStatistics(Pageable pageRequest) {
        final Map<String, String> sortableAttributesMap = Map.of(
                "productName", "name",
                "revenue", "total_revenue",
                "orderedQuantity", "total_ordered_quantity"
        );
        Map<String, Object> sqlParameters = PaginationUtil.buildSQLPagination(pageRequest, sortableAttributesMap);
        List<OrderedProductStatDTO> result = sqlSession.selectList(NAMESPACE + ".orderedProductStatistics", sqlParameters);
        return new OrderedProductStatPagingResponse(
                pageRequest.getPageNumber() + 1,
                pageRequest.getPageSize(),
                result
        );
    }

    @Override
    public ProductRepeatPurchaseRatePagingResponse getRepeatPurchaseRateStatistics(Pageable pageRequest) {
        final Map<String, String> sortableAttributesMap = Map.of(
                "uniqueBuyer", "count_unique_buyers",
                "frequentBuyer", "count_frequent_buyers",
                "repeatPurchaseRate", "repeat_purchase_rate"
        );
        Map<String, Object> sqlParameters = PaginationUtil.buildSQLPagination(pageRequest, sortableAttributesMap);
        List<ProdRepeatPurchaseRateDTO> result = sqlSession.selectList(NAMESPACE + ".repeatPurchaseRate", sqlParameters);
        return new ProductRepeatPurchaseRatePagingResponse(
                pageRequest.getPageNumber() + 1,
                pageRequest.getPageSize(),
                result
        );
    }
}