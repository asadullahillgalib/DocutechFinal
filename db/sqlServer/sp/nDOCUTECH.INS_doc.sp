
/****** Object:  StoredProcedure [nDOCUTECH].[INS_doc]    Script Date: 9/11/2019 6:09:54 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


	CREATE PROC [nDOCUTECH].[INS_doc]

		 @tx_action_name		varchar(32)				= NULL	OUTPUT
	
		, @is_active			int						= 1
		, @id_env_key			int						= NULL	OUTPUT
		, @id_user_mod_key 		int						= NULL	OUTPUT
		, @dtt_mod				datetime				= NULL	OUTPUT
	
		, @id_state_key			int						= NULL	OUTPUT
		, @tx_state_name		varchar(64)				= NULL	OUTPUT
		, @id_action_key		int						= NULL  OUTPUT
	
		, @id_event_key			int						= NULL	OUTPUT
		, @id_event_map1_key	int						= NULL	OUTPUT
		, @id_event_map2_key	int						= NULL	OUTPUT
		, @id_event_map3_key	int						= NULL	OUTPUT
		, @id_event_map4_key	int						= NULL	OUTPUT
	
		, @dtt_valid_from		datetime				= NULL
		, @dtt_valid_to			datetime				= NULL
		, @dtt_as_at			datetime				= NULL
		, @dtt_last_refresh		datetime				= NULL

		, @id_doc_key 			    int 					 	= NULL 	OUTPUT
		, @id_doc_ver 			    int 						= NULL 	OUTPUT
		, @supplier_number 			int 						= NULL
		, @tx_company_address 		varchar(256)				= NULL
		, @tx_invoice_to 			varchar(256)				= NULL
		, @tx_deliver_to 			varchar(256)				= NULL
		, @tx_currency 				varchar(8)					= NULL
		, @receipt_number 			int 						= NULL
		, @document_number 			int 						= NULL
		, @dt_document				date 						= NULL
		, @tx_association_number 	varchar(256)				= NULL
		, @recipient_of_invoice 	int 						= NULL
		, @vat_number 				int 						= NULL
		, @invoice_number 			int 						= NULL
		, @tx_account_number 		varchar(256)				= NULL
		, @tx_voucher_number 		varchar(256)				= NULL
		, @tx_fax_number 			varchar(128)				= NULL
		, @dt_despatch 				date 						= NULL
		, @delivery_note_no 		int  						= NULL

		, @tx_order_id 				varchar(256)				= NULL
		, @tx_delivery_note 		varchar(256)				= NULL
		, @tx_delivery_details 		varchar(256)				= NULL
		, @flt_net_invoice_total 	float(8)					= NULL
		, @flt_vat 					float(8)					= NULL
		, @flt_vat_rate 			float(8)					= NULL
		, @flt_vat_payable 			float(8)					= NULL
		, @flt_invoice_amount 		float(8)					= NULL
		, @flt_total_before_vat 	float(8)					= NULL
		, @flt_total_amount_due 	float(8)					= NULL
		, @tx_payment_details 		varchar(256)				= NULL
		, @flt_discount 			float(8)					= NULL

	                  
		, @is_sel_data			int						= NULL
		, @tx_log_level			varchar(32)				= NULL
		, @id_log_level			int						= NULL
		, @is_record_time		bit						= NULL
		, @is_print_msg			bit						= NULL
		, @is_persist_msg		bit						= NULL
		, @tx_json_log_msg		varchar(MAX)			= NULL	OUTPUT     																
	AS	                       
	BEGIN                          	
		
	-- GLOBAL VARS --
	DECLARE	  @g_tx_action_name_orig	varchar(32)
			, @g_tx_state_name_orig		varchar(64)

			, @g_id_env_key				int
			, @g_tx_env_name			varchar(256)

			, @g_ct_row					int
			, @g_id_return_status		int

			, @g_id_error_key			int
			, @g_tx_err_msg				varchar(1024)
			, @g_tx_err_msg_tmp			varchar(1024)
			, @g_is_outer_sp			bit

	DECLARE   @g_dt_sys_today			date
			, @g_dt_sys_prev_day		date
			, @g_dt_sys_next_day		date
			, @g_dt_sys_prev_biz_day	date
			, @g_dt_sys_next_biz_day	date

	

	--	DECLARE @g_in_tran		int
	DECLARE	@g_is_ext_txn	bit
	DECLARE @g_is_sp_txn	bit

	SELECT @g_is_ext_txn = 0, @g_is_sp_txn = 0

	DECLARE @g_tmp_int		int

	DECLARE   @g_dtt_log			datetime
	 		, @g_tx_tmp_log_msg		varchar(MAX)
	 		, @g_tx_log_msg			varchar(MAX)

		  	, @g_id_err_num			int
			, @g_id_err_sev			int
			, @g_id_err_state		int
			, @g_tx_sp_name			varchar(128)
			, @g_id_line_num		int
			, @g_id_log_level		int

			, @g_is_record_time		bit
			, @g_is_print_msg		bit
			, @g_is_persist_msg  	bit

			, @g_tx_json_log_msg	varchar(MAX)

	SELECT	@g_tx_json_log_msg = ''

	DECLARE @g_dtt_proc_start		datetime
	DECLARE @g_dtt_proc_end			datetime
	DECLARE @g_dtt_tot_elapsed		int

	DECLARE	@g_dtt_query_start		datetime
	DECLARE	@g_dtt_query_end		datetime
	DECLARE @g_dtt_query_elapsed	int

	DECLARE @g_tmp_tx_tot_time		varchar(255)
	DECLARE @g_tmp_tx_query_time	varchar(255)

	IF ( (@@NESTLEVEL = 1) )
	BEGIN
		IF ( ((@@OPTIONS & 2) != 2) AND (@@TRANCOUNT = 0) )
		BEGIN
			SELECT @g_is_ext_txn = 0
		END
		ELSE
		BEGIN
			SELECT @g_is_ext_txn = 1
		END
	END

	IF (((@tx_log_level IS NULL) OR (@tx_log_level = '?')) OR ((@is_persist_msg IS NULL) OR (@is_persist_msg = -2147483648)) OR ((@is_record_time IS NULL) OR (@is_record_time = -2147483648)))
	BEGIN
		EXEC @g_id_return_status = GET_sp_log_level @tx_sp_name='INS_doc', @tx_log_level=@tx_log_level OUTPUT, @id_log_level=@id_log_level OUTPUT, @is_record_time=@is_record_time OUTPUT, @is_print_msg=@is_print_msg OUTPUT, @is_persist_msg=@is_persist_msg OUTPUT
	END

	SELECT @g_is_record_time = @is_record_time, @g_is_print_msg = @is_print_msg, @g_is_persist_msg	= @is_persist_msg, @g_id_log_level = @id_log_level

	SELECT @g_tx_err_msg = 'Entering[INS_doc] : @tx_action[' + ISNULL(@tx_action_name, '?') + ']'
	SELECT @g_dtt_log = GETDATE(), @g_tx_sp_name = 'INS_doc.sp', @g_id_line_num = 142

	-- TODO_H : [Naz] Move to Function

	IF ( NOT (((@g_id_log_level IS NULL) OR (@g_id_log_level = -2147483648))) )
	BEGIN
		IF (2 >= @g_id_log_level)
		BEGIN
			IF (@g_is_print_msg = 1)
			BEGIN
				SELECT @g_tx_tmp_log_msg = '[' + @g_tx_sp_name + ':' + CONVERT(varchar, @g_id_line_num) + '] | ' + CONVERT(varchar, @g_dtt_log) + ' | ' + 'INFO ' + ' | ' + '?' + ' | [' + CONVERT(varchar, 0) + '] -> ' + ISNULL(@g_tx_err_msg, '?')
				PRINT  @g_tx_tmp_log_msg
			END

			IF ( (@g_is_persist_msg = 1) )
			BEGIN
				EXEC @g_id_return_status = ACT_msg_log   @tx_action_name = 'NEW', @id_env_key = @g_id_env_key, @id_user_mod_key = @id_user_mod_key, @dtt_mod = @g_dtt_log
													   , @id_state_key = @id_state_key, @id_action_key = @id_action_key, @tx_log_msg_type='?', @tx_log_level='INFO ', @id_log_level=2
													   , @tx_sp_name=@g_tx_sp_name, @id_line_num=@g_id_line_num, @id_err_code=0, @tx_log_msg=@g_tx_err_msg, @tx_json_log_msg = @tx_json_log_msg OUTPUT
			
				IF (@g_id_return_status != 0)
				BEGIN
					RAISERROR ('Error calling ACT_msg_log', 16, 1)
					IF (@g_is_sp_txn = 1)
					BEGIN
						SELECT @g_is_sp_txn = 0
						ROLLBACK TRANSACTION
				
						IF ( (@@NESTLEVEL = 1) )
						BEGIN
							EXEC @g_id_return_status = ACT_msg_log @tx_action_name = 'LOG_JSON', @tx_json_log_msg = @tx_json_log_msg, @ct_nestlevel = @@NESTLEVEL
					
							IF (@g_id_return_status != 0)
							BEGIN
								PRINT 'ERROR SAVING JSON LOG MSG!'
							END
						END
					END
					IF ( (@@NESTLEVEL = 1) AND (@g_is_ext_txn = 1) )
					BEGIN
						-- SELECT @tx_json_log_msg = 'JSON_LOG:[' + @tx_json_log_msg + ']'
				
						IF (30008 = 0)
						BEGIN
							--RAISERROR (@tx_json_log_msg, 10, 1)
							EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 10
				
						END
						ELSE
						BEGIN
							--RAISERROR (@tx_json_log_msg, 16, 1)
							EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 16
						END
					END
				
					RETURN 30008
				END
			END
		END
	END

	EXEC @g_id_return_status = GET_environment @id_env_key = @g_id_env_key OUTPUT, @tx_env_name	= @g_tx_env_name OUTPUT, @id_user_key = @id_user_mod_key

	SELECT @g_tx_err_msg = 'Error calling SP -> [GET_ENVIRONMENT] '

	IF (@g_id_return_status != 0)
	BEGIN
		RAISERROR (@g_tx_err_msg, 16, 1)
		IF (@g_is_sp_txn = 1)
		BEGIN
			SELECT @g_is_sp_txn = 0
			ROLLBACK TRANSACTION
	
			IF ( (@@NESTLEVEL = 1) )
			BEGIN
				EXEC @g_id_return_status = ACT_msg_log @tx_action_name = 'LOG_JSON', @tx_json_log_msg = @tx_json_log_msg, @ct_nestlevel = @@NESTLEVEL
		
				IF (@g_id_return_status != 0)
				BEGIN
					PRINT 'ERROR SAVING JSON LOG MSG!'
				END
			END
		END
		IF ( (@@NESTLEVEL = 1) AND (@g_is_ext_txn = 1) )
		BEGIN
			-- SELECT @tx_json_log_msg = 'JSON_LOG:[' + @tx_json_log_msg + ']'
	
			IF (30001 = 0)
			BEGIN
				--RAISERROR (@tx_json_log_msg, 10, 1)
				EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 10
	
			END
			ELSE
			BEGIN
				--RAISERROR (@tx_json_log_msg, 16, 1)
				EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 16
			END
		END
	
		RETURN 30001
	END

	IF (((@dtt_mod IS NULL) OR (@dtt_mod = '01/01/1970')))
	BEGIN
		SELECT @dtt_mod = GETDATE()
	END

	IF (@tx_action_name != 'LOGIN')
	BEGIN
		IF (((@id_user_mod_key IS NULL) OR (@id_user_mod_key = -2147483648)))
		BEGIN
			RAISERROR ('Error:Int [@id_user_mod_key] should not be NULL', 16, 1)
			IF (@g_is_sp_txn = 1)
		BEGIN
			SELECT @g_is_sp_txn = 0
			ROLLBACK TRANSACTION
	
			IF ( (@@NESTLEVEL = 1) )
			BEGIN
				EXEC @g_id_return_status = ACT_msg_log @tx_action_name = 'LOG_JSON', @tx_json_log_msg = @tx_json_log_msg, @ct_nestlevel = @@NESTLEVEL
		
				IF (@g_id_return_status != 0)
				BEGIN
					PRINT 'ERROR SAVING JSON LOG MSG!'
				END
			END
		END
			IF ( (@@NESTLEVEL = 1) AND (@g_is_ext_txn = 1) )
			BEGIN
				-- SELECT @tx_json_log_msg = 'JSON_LOG:[' + @tx_json_log_msg + ']'
		
				IF (30011 = 0)
				BEGIN
					--RAISERROR (@tx_json_log_msg, 10, 1)
					EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 10
		
				END
				ELSE
				BEGIN
					--RAISERROR (@tx_json_log_msg, 16, 1)
					EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 16
				END
			END
		
			RETURN 30011
		END
	END

	-- X_SET_NULL_ACTION

	SELECT  @g_tx_action_name_orig = @tx_action_name   
	
	EXEC @g_id_return_status = GET_system_key @id_env_key	= @g_id_env_key, @tx_key_name = 'id_doc_key', @id_key_value	= @id_doc_key OUTPUT, @num_keys = 1

	SELECT @g_tx_err_msg = 'Error generating key for id_doc_key'
	IF (@g_id_return_status != 0)
	BEGIN
		RAISERROR (@g_tx_err_msg, 16, 1)
		IF (@g_is_sp_txn = 1)
		BEGIN
			SELECT @g_is_sp_txn = 0
			ROLLBACK TRANSACTION
	
			IF ( (@@NESTLEVEL = 1) )
			BEGIN
				EXEC @g_id_return_status = ACT_msg_log @tx_action_name = 'LOG_JSON', @tx_json_log_msg = @tx_json_log_msg, @ct_nestlevel = @@NESTLEVEL
		
				IF (@g_id_return_status != 0)
				BEGIN
					PRINT 'ERROR SAVING JSON LOG MSG!'
				END
			END
		END
		IF ( (@@NESTLEVEL = 1) AND (@g_is_ext_txn = 1) )
		BEGIN
			-- SELECT @tx_json_log_msg = 'JSON_LOG:[' + @tx_json_log_msg + ']'
	
			IF (30003 = 0)
			BEGIN
				--RAISERROR (@tx_json_log_msg, 10, 1)
				EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 10
	
			END
			ELSE
			BEGIN
				--RAISERROR (@tx_json_log_msg, 16, 1)
				EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 16
			END
		END
	
		RETURN 30003
	END

    IF ( ((@id_doc_ver IS NULL) OR (@id_doc_ver = -2147483648)) )
	BEGIN
		SELECT @id_doc_ver = 0
	END

	IF (NOT (@g_is_ext_txn = 1 OR @@trancount > 0) )
	BEGIN
		SELECT @g_is_sp_txn = 1
		BEGIN TRANSACTION
	END

		INSERT INTO nDOCUTECH.T_DOC
		(
						
			  id_doc_key				
			, id_doc_ver					
			, -- id_doc_ver
			 is_active
			, id_env_key
			, id_user_mod_key
			, dtt_mod
			, id_event_key
			, id_state_key
			, id_action_key
			    
			, supplier_number 		
			, tx_company_address 		
			, tx_invoice_to 			
			, tx_deliver_to 			
			, tx_currency 				
			, receipt_number 			
			, document_number 		
			, dt_document				 	
			, tx_association_number
			, recipient_of_invoice 
			, vat_number 						
			, invoice_number 			
			, tx_account_number 		
			, tx_voucher_number 		
			, tx_fax_number 		
			, dt_despatch 		 
			, delivery_note_no 	
			, tx_order_id 			
			, tx_delivery_note 		
			, tx_delivery_details 	
			, flt_net_invoice_total
			, flt_vat 					
			, flt_vat_rate 			
			, flt_vat_payable 			
			, flt_invoice_amount 		
			, flt_total_before_vat 
			, flt_total_amount_due 
			, tx_payment_details 		
			, flt_discount 								
				
		)
		VALUES
		(
			  @id_doc_key
			, @id_doc_ver
			, --  ISNULL(@id_doc_ver			, 0)
			  ISNULL(@is_active						, 1			)
			, ISNULL(@id_env_key					, 0			)
		   	, ISNULL(@id_user_mod_key				, 0			)
			, ISNULL(@dtt_mod						, GETDATE()	)
			, ISNULL(@id_event_key					, 0			)
			, ISNULL(@id_state_key					, 0			)
			, ISNULL(@id_action_key					, 0			)

			, ISNULL(@supplier_number 				, '?'		)
			, ISNULL(@tx_company_address 			, '?'		)
			, ISNULL(@tx_invoice_to 				, '?'		)
			, ISNULL(@tx_deliver_to 				, '?'		)
			, ISNULL(@tx_currency 					, '?'		)
			, ISNULL(@receipt_number 				, '?'		)
			, ISNULL(@document_number 				, '?'		)
			, ISNULL(@dt_document					, '?'		)
			, ISNULL(@tx_association_number			, '?'		)
			, ISNULL(@recipient_of_invoice 			, '?'		)
			, ISNULL(@vat_number 					, '?'		)
			, ISNULL(@invoice_number 				, '?'		)
			, ISNULL(@tx_account_number 			, '?'		)
			, ISNULL(@tx_voucher_number 			, '?'		)
			, ISNULL(@tx_fax_number 				, '?'		)
			, ISNULL(@dt_despatch 		 			, '?'		)
			, ISNULL(@delivery_note_no 				, '?'		)
			, ISNULL(@tx_order_id 					, '?'		)
			, ISNULL(@tx_delivery_note 				, '?'		)
			, ISNULL(@tx_delivery_details 			, '?'		)
			, ISNULL(@flt_net_invoice_total			, '?'		)
			, ISNULL(@flt_vat 						, '?'		)
			, ISNULL(@flt_vat_rate 					, '?'		)
			, ISNULL(@flt_vat_payable 				, '?'		)
			, ISNULL(@flt_invoice_amount 			, '?'		)
			, ISNULL(@flt_total_before_vat 			, '?'		)
			, ISNULL(@flt_total_amount_due 			, '?'		)
			, ISNULL(@tx_payment_details 			, '?'		)
			, ISNULL(@flt_discount 					, '?'		)	
		
		)

		SELECT @g_ct_row = @@rowcount, @g_id_error_key = @@error		
		SELECT @g_tx_err_msg = 'Error encountered while inserting -> nDOCUTECH.T_DOC' + '@id_doc_key [' + CONVERT(varchar, @id_doc_key) + '] '
		IF ( (@g_ct_row = 0) OR (@g_id_error_key != 0) )
		BEGIN
			RAISERROR (@g_tx_err_msg, 16, 1)
			IF (@g_is_sp_txn = 1)
		BEGIN
			SELECT @g_is_sp_txn = 0
			ROLLBACK TRANSACTION
	
			IF ( (@@NESTLEVEL = 1) )
			BEGIN
				EXEC @g_id_return_status = ACT_msg_log @tx_action_name = 'LOG_JSON', @tx_json_log_msg = @tx_json_log_msg, @ct_nestlevel = @@NESTLEVEL
		
				IF (@g_id_return_status != 0)
				BEGIN
					PRINT 'ERROR SAVING JSON LOG MSG!'
				END
			END
		END
			IF ( (@@NESTLEVEL = 1) AND (@g_is_ext_txn = 1) )
			BEGIN
				-- SELECT @tx_json_log_msg = 'JSON_LOG:[' + @tx_json_log_msg + ']'
		
				IF (30008 = 0)
				BEGIN
					--RAISERROR (@tx_json_log_msg, 10, 1)
					EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 10
		
				END
				ELSE
				BEGIN
					--RAISERROR (@tx_json_log_msg, 16, 1)
					EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 16
				END
			END
		
			RETURN 30008
		END

		

	IF (@g_is_sp_txn = 1)
	BEGIN
		SELECT @g_is_sp_txn = 0
		COMMIT TRANSACTION
		
		SELECT @tx_json_log_msg = ''
	END

	IF ( (@g_is_record_time = 1) )
		SELECT @g_dtt_proc_end = GETDATE()
	IF ( (@g_is_record_time = 1) )
		SELECT @g_dtt_tot_elapsed  = datediff(ss, @g_dtt_proc_start, @g_dtt_proc_end)

	SELECT @g_tx_err_msg = 'Exiting[INS_doc] : @tx_action[' + ISNULL(@tx_action_name, '?') + ']'
	SELECT @g_dtt_log = GETDATE(), @g_tx_sp_name = 'INS_doc.sp', @g_id_line_num = 450

	-- TODO_H : [Naz] Move to Function

	IF ( NOT (((@g_id_log_level IS NULL) OR (@g_id_log_level = -2147483648))) )
	BEGIN
		IF (2 >= @g_id_log_level)
		BEGIN
			IF (@g_is_print_msg = 1)
			BEGIN
				SELECT @g_tx_tmp_log_msg = '[' + @g_tx_sp_name + ':' + CONVERT(varchar, @g_id_line_num) + '] | ' + CONVERT(varchar, @g_dtt_log) + ' | ' + 'INFO ' + ' | ' + '?' + ' | [' + CONVERT(varchar, 0) + '] -> ' + ISNULL(@g_tx_err_msg, '?')
				PRINT  @g_tx_tmp_log_msg
			END

			IF ( (@g_is_persist_msg = 1) )
			BEGIN
				EXEC @g_id_return_status = ACT_msg_log   @tx_action_name = 'NEW', @id_env_key = @g_id_env_key, @id_user_mod_key = @id_user_mod_key, @dtt_mod = @g_dtt_log
													   , @id_state_key = @id_state_key, @id_action_key = @id_action_key, @tx_log_msg_type='?', @tx_log_level='INFO ', @id_log_level=2
													   , @tx_sp_name=@g_tx_sp_name, @id_line_num=@g_id_line_num, @id_err_code=0, @tx_log_msg=@g_tx_err_msg, @tx_json_log_msg = @tx_json_log_msg OUTPUT
			
				IF (@g_id_return_status != 0)
				BEGIN
					RAISERROR ('Error calling ACT_msg_log', 16, 1)
					IF (@g_is_sp_txn = 1)
					BEGIN
						SELECT @g_is_sp_txn = 0
						ROLLBACK TRANSACTION
				
						IF ( (@@NESTLEVEL = 1) )
						BEGIN
							EXEC @g_id_return_status = ACT_msg_log @tx_action_name = 'LOG_JSON', @tx_json_log_msg = @tx_json_log_msg, @ct_nestlevel = @@NESTLEVEL
					
							IF (@g_id_return_status != 0)
							BEGIN
								PRINT 'ERROR SAVING JSON LOG MSG!'
							END
						END
					END
					IF ( (@@NESTLEVEL = 1) AND (@g_is_ext_txn = 1) )
					BEGIN
						-- SELECT @tx_json_log_msg = 'JSON_LOG:[' + @tx_json_log_msg + ']'
				
						IF (30008 = 0)
						BEGIN
							--RAISERROR (@tx_json_log_msg, 10, 1)
							EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 10
				
						END
						ELSE
						BEGIN
							--RAISERROR (@tx_json_log_msg, 16, 1)
							EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 16
						END
					END
				
					RETURN 30008
				END
			END
		END
	END

	IF ( (@@NESTLEVEL = 1) AND (@g_is_ext_txn = 1) )
	BEGIN
		-- SELECT @tx_json_log_msg = 'JSON_LOG:[' + @tx_json_log_msg + ']'

		IF (0 = 0)
		BEGIN
			--RAISERROR (@tx_json_log_msg, 10, 1)
			EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 10

		END
		ELSE
		BEGIN
			--RAISERROR (@tx_json_log_msg, 16, 1)
			EXEC SYS_split_raise_error @tx_data_orig = @tx_json_log_msg, @tx_separator = 'DB_NEW_LINE', @id_severity_key = 16
		END
	END

	RETURN 0

	RETURN 0
END
